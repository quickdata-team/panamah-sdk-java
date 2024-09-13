package br.com.quickdata.panamah.sdk;

import br.com.quickdata.panamah.sdk.model.*;
import br.com.quickdata.panamah.sdk.nfe.Det;
import br.com.quickdata.panamah.sdk.nfe.NFe;
import br.com.quickdata.panamah.sdk.nfe.NFeProc;
import br.com.quickdata.panamah.sdk.nfe.Prod;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class PanamahTask extends TimerTask {

    private PanamahConfig config;
    private PanamahLote loteAtual = new PanamahLote();
    private long ultimoEnvio = new Date().getTime();
    private PanamahListener onSendSuccess;
    private PanamahListener onError;
    private boolean fechandoLote;

    public PanamahTask(PanamahConfig config) throws IOException {
        this.config = config;
        restauraLoteAtual();
    }

    @Override
    public void run() {
        verificaFechamento();
        verificaEnvio();
        removeAntigos();
    }

    public boolean isFechandoLote() {
        return fechandoLote;
    }

    public PanamahListener getOnSendSuccess() {
        return onSendSuccess;
    }

    public void setOnSendSuccess(PanamahListener onSendSuccess) {
        this.onSendSuccess = onSendSuccess;
    }

    public PanamahListener getOnError() {
        return onError;
    }

    public void setOnError(PanamahListener onError) {
        this.onError = onError;
    }

    public void verificaEnvio() {
        try {
            if (new Date().getTime() > ultimoEnvio / 2 + config.getTtl()) {
                // System.out.println("verificaEnvio");
                enviaLote();
            }
        } catch (Exception e) {
            if (onError != null)
                onError.notify(new PanamahEvent(config, loteAtual, e));
            e.printStackTrace();
        }
    }

    public void removeAntigos() {
        try {
            Path lotes = Paths.get(config.getBasePath(), "lotes", "enviados");
            Files.walkFileTree(lotes, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    long age = System.currentTimeMillis() - attrs.creationTime().toMillis();
                    if (age > config.getMaxAgeSent())
                        file.toFile().delete();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            System.out.println("Problema ao limpar lotes enviados");
        }

    }

    public void verificaFechamento() {
        try {
            if (loteAtual.isVelho(config) || isLoteAtualCheio()) {
                // System.out.println("verificaFechamento");
                fechaLoteAtual();
            }
        } catch (Exception e) {
            if (onError != null) {
                PanamahEvent ev = new PanamahEvent(config, loteAtual, e);
                onError.notify(ev);
                e.printStackTrace();
            }
        }
    }

    public void restauraLoteAtual() throws FileNotFoundException, IOException {
        if (!Paths.get(config.getBasePath(), "lotes").toFile().exists())
            Paths.get(config.getBasePath(), "lotes").toFile().mkdirs();
        File f = Paths.get(config.getBasePath(), "lotes", "loteatual.json").toFile();
        if (f.exists()) {
            try (Reader r = new FileReader(f)) {
                loteAtual = PanamahUtil.buildGson().fromJson(r, PanamahLote.class);
                if (loteAtual == null)
                    loteAtual = new PanamahLote(); // paranoia
            }
        } else {
            try (Writer w = new BufferedWriter(new FileWriter(f))) {
                w.write(PanamahUtil.buildGson().toJson(loteAtual));
            }
        }
    }

    public void persisteLoteAtual() throws IOException {
        if (!Paths.get(config.getBasePath(), "lotes").toFile().exists())
            Paths.get(config.getBasePath(), "lotes").toFile().mkdirs();
        File f = Paths.get(config.getBasePath(), "lotes", "loteatual.json").toFile();
        try (Writer w = new BufferedWriter(new FileWriter(f))) {
            w.write(PanamahUtil.buildGson().toJson(loteAtual));
        }
        if (isLoteAtualCheio()) {
            fechaLoteAtual();
            verificaEnvio();
        }
    }

    public synchronized void fechaLoteAtual() throws FileNotFoundException, IOException {
        try {
            fechandoLote = true;
            if (loteAtual.isVazio())
                return; // lote vazio é apenas sobrescrito
            loteAtual.setStatus(PanamahStatusLote.FECHADO);
            String fileName = "lote-" + PanamahUtil.stamp(loteAtual.getUltimaAtualizacao()) + ".json";
            if (!Paths.get(config.getBasePath(), "lotes", "fechados").toFile().exists())
                Paths.get(config.getBasePath(), "lotes", "fechados").toFile().mkdirs();
            File f = Paths.get(config.getBasePath(), "lotes", "fechados", fileName).toFile();
            LinkedBlockingQueue<PanamahOperacao<?>> restante = loteAtual.removeExcedente();
            try (Writer w = new BufferedWriter(new FileWriter(f))) {
                w.write(PanamahUtil.buildGson().toJson(loteAtual));
            }
            abreNovoLote(restante);
        } finally {
            fechandoLote = false;
        }
    }

    private void abreNovoLote(LinkedBlockingQueue<PanamahOperacao<?>> restante) throws IOException {
        loteAtual = new PanamahLote();
        if (restante != null)
            loteAtual.getOperacoes().addAll(restante);
        File f = Paths.get(config.getBasePath(), "lotes", "loteatual.json").toFile();
        try (Writer w = new BufferedWriter(new FileWriter(f))) {
            w.write(PanamahUtil.buildGson().toJson(loteAtual));
        }
    }

    public void enviaLote() throws Exception {
        // atualiza primeiro pra não ficar jogando erros o tempo inteiro
        ultimoEnvio = new Date().getTime();
        Path fechados = Paths.get(config.getBasePath(), "lotes", "fechados");
        if (!fechados.toFile().exists())
            fechados.toFile().mkdirs();
        Path enviados = Paths.get(config.getBasePath(), "lotes", "enviados");
        if (!enviados.toFile().exists())
            enviados.toFile().mkdirs();
        File toSend = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fechados)) {
            for (Path p : stream) {
                File f = p.toFile();
                if (f.isDirectory())
                    continue;
                if (!f.getName().startsWith("lote-"))
                    continue;
                if (!f.getName().endsWith(".json"))
                    continue;
                toSend = f;
                break;
            }
        }
        // File[] files = Paths.get(config.getBasePath(), "lotes",
        // "fechados").toFile()//
        // .listFiles(new FilenameFilter() {
        // @Override
        // public boolean accept(File dir, String name) {
        // return name.startsWith("lote-") && name.endsWith(".json");
        // }
        // });
        // Sem lote para enviar
        if (toSend == null) return;
        try (Reader r = new BufferedReader(new FileReader(toSend))) {
            PanamahLote lote = PanamahUtil.buildGson().fromJson(r, PanamahLote.class);
            // não envia lote vazio
            if (lote != null && lote.getOperacoes() != null && lote.getOperacoes().size() > 0) {
                PanamahRetornoLote ret = PanamahUtil.buildGson()//
                        .fromJson(PanamahUtil.send(config, lote), PanamahRetornoLote.class);

                lote.setStatus(PanamahStatusLote.ENVIADO);

                File toWrite = Paths.get(config.getBasePath(), "lotes", "enviados", toSend.getName()).toFile();

                try (Writer w = new BufferedWriter(new FileWriter(toWrite))) {
                    w.write(PanamahUtil.buildGson().toJson(lote));
                }

                if (ret != null && ret.getFalhas() != null)
                    trataFalhas(lote, ret);

                if (onSendSuccess != null) {
                    PanamahEvent e = new PanamahEvent(config, lote, ret);
                    onSendSuccess.notify(e);
                }
            }
            // sempre deleta
        }
        toSend.delete();
    }

    private void trataFalhas(PanamahLote lote, PanamahRetornoLote ret) {
        ArrayList<String> idsfalhas = new ArrayList<String>();
        for (PanamahRetornoItem item : ret.getFalhas().getItens())
            idsfalhas.add(item.getId());

        ArrayList<PanamahOperacao<?>> ops = new ArrayList<PanamahOperacao<?>>();
        for (PanamahOperacao<?> op : lote.getOperacoes()) {
            @SuppressWarnings("unchecked")
            LinkedTreeMap<String, String> m = (LinkedTreeMap<String, String>) op.getData();
            String id = (String) m.get("id");
            if (idsfalhas.indexOf(id) > -1)
                ops.add(op);
        }
        loteAtual.addFalhas(ops);
    }

    public PanamahLote getLoteAtual() {
        return loteAtual;
    }

    public synchronized boolean isLoteAtualCheio() {
        int len = PanamahUtil.buildGson().toJson(loteAtual).getBytes().length;
        return len >= config.getMaxBytes() || loteAtual.getOperacoes().size() > config.getMaxItens();
    }

    public void readNFe(String filePath) throws Exception {
        String s = new String(Files.readAllBytes(Paths.get(filePath)));
        if (s == null || "".equals(s))
            throw new Exception("invalid filePath");
        if (s.indexOf("nfeProc") > -1)
            processNFeProc(s);
        else if (s.indexOf("NFe") > -1)
            processNFe(s);
    }

    private void processNFeProc(String s) throws Exception {
        NFeProc proc = (NFeProc) PanamahUtil.buildXStream().fromXML(s);
        proccessNFe(proc.getNfe());
    }

    private void processNFe(String s) throws Exception {
        NFe nfe = (NFe) PanamahUtil.buildXStream().fromXML(s);
        proccessNFe(nfe);
    }

    private PanamahNFeModel proccessNFe(NFe nfe) throws Exception {
        PanamahNFeModel model = new PanamahNFeModel();
        // Loja a partir do emitente
        PanamahLoja loja = new PanamahLoja();
        loja.setId(nfe.getInfNFe().getEmit().getCnpj());
        loja.setNumeroDocumento(nfe.getInfNFe().getEmit().getCnpj());
        loja.setDescricao(nfe.getInfNFe().getEmit().getxNome());
        loja.setBairro(nfe.getInfNFe().getEmit().getEnderEmit().getxBairro());
        loja.setCidade(nfe.getInfNFe().getEmit().getEnderEmit().getxMun());
        loja.setUf(nfe.getInfNFe().getEmit().getEnderEmit().getUF());
        loja.setLogradouro(nfe.getInfNFe().getEmit().getEnderEmit().getxLgr());
        loja.setNumero("" + nfe.getInfNFe().getEmit().getEnderEmit().getNro());
        loja.setComplemento(nfe.getInfNFe().getEmit().getEnderEmit().getxCpl());
        loja.setAtiva(true);
        loja.setMatriz(true);
        loja.setHoldingId(nfe.getInfNFe().getEmit().getCnpj());
        loja.setRamo(nfe.getInfNFe().getEmit().getCnpj());

        model.setLoja(loja);

        // Cliente
        if (nfe.getInfNFe().getDest() != null) {

            PanamahCliente cliente = new PanamahCliente();
            cliente.setId(nfe.getInfNFe().getDest().getCpf());
            String doc = nfe.getInfNFe().getDest().getCpf();
            if (doc == null)
                doc = nfe.getInfNFe().getDest().getCnpj();
            cliente.setNumeroDocumento(doc);
            cliente.setNome(nfe.getInfNFe().getDest().getxNome());
            cliente.setRamo("");
            cliente.setUf(nfe.getInfNFe().getDest().getEnderDest().getUf());
            cliente.setCidade(nfe.getInfNFe().getDest().getEnderDest().getxMun());
            cliente.setBairro(nfe.getInfNFe().getDest().getEnderDest().getxBairro());

            model.setCliente(cliente);

        }

        // Fornecedor
        // PanamahFornecedor fornecedor = new PanamahFornecedor();
        // fornecedor.setId(nfe.getInfNFe().g);

        // Produto

        // Venda
        if (nfe.getInfNFe().getDet() != null) {
            PanamahVenda venda = new PanamahVenda();
            venda.setId(nfe.getInfNFe().getId());
            venda.setLojaId(nfe.getInfNFe().getEmit().getCnpj());
            // venda.setClienteId(nfe.getInfNFe().getDest().getCpf());
            venda.setData(nfe.getInfNFe().getIde().getDhEmi());
            venda.setDataHoraVenda(nfe.getInfNFe().getIde().getDhEmi());
            venda.setItens(new ArrayList<PanamahVendaItens>());
            venda.setEfetiva(true);
            venda.setSequencial("" + nfe.getInfNFe().getIde().getSerie());
            venda.setTipoPreco("" + nfe.getInfNFe().getIde().getMod());
            venda.setValor(nfe.getInfNFe().getTotal().getIcmsTot().getvNF());
            venda.setPagamentos(new ArrayList<PanamahVendaPagamentos>());

            for (Det d : nfe.getInfNFe().getDet()) {
                PanamahVendaItens i = new PanamahVendaItens();
                Prod produto = d.getProd();
                i.setProdutoId(produto.getcProd());
                i.setQuantidade(produto.getqCom());
                i.setPreco(produto.getvProd());
                i.setValorUnitario(produto.getvUnCom());
                i.setValorTotal(produto.getvProd());
                i.setDesconto(produto.getvDesc());
                i.setEfetivo(true);
                venda.getItens().add(i);
            }

            venda.setQuantidadeItens(new BigDecimal(1.0 * nfe.getInfNFe().getDet().size()));

            model.setVenda(venda);
        }
        return model;
    }

    public void deletaLoteAtual() throws Exception {
        Path p = Paths.get(config.getBasePath(), "loteatual.json");
        if (Files.exists(p))
            Files.delete(p);
        loteAtual = new PanamahLote();

    }

    public PanamahConfig getConfig() {
        return config;
    }

    public HashMap<String, PanamahPendencias> pending(int start, int count) throws Exception {
        return PanamahUtil.pending(config, start, count);
    }

}
