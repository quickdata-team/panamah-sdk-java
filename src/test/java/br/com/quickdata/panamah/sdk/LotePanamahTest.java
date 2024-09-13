package br.com.quickdata.panamah.sdk;

import br.com.quickdata.panamah.sdk.model.PanamahEan;
import org.junit.Test;

public class LotePanamahTest {

    private static PanamahConfig c = PanamahConfig.fromEnv("staging");

    @Test
    public void deveFecharLoteCheio() throws Exception {
        PanamahStream p = PanamahStream.init(c);
        int i = 500;
        while (i-- > 0) {
            PanamahEan ean = new PanamahEan();
            ean.setId("1");
            ean.setProdutoId("1");
            p.save(ean);
        }
//        assertEquals(0, p.getTask().getLoteAtual().getOperacoes().size());
        p.flush(true);
    }

    @Test
    public void deveMoverExcedenteLote() throws Exception {
        PanamahStream p = PanamahStream.init(c);
        int i = 600;
        while (i-- > 0) {
            PanamahEan ean = new PanamahEan();
            ean.setId("1");
            ean.setProdutoId("1");
            p.save(ean);
        }
//        assertEquals(100, p.getTask().getLoteAtual().getOperacoes().size());
        p.flush(true);
    }

    @Test
    public void deveriaTolerarConcorrencia() throws Exception {
        final PanamahStream p = PanamahStream.init(c);
        // java 8 codelevel disabled
//        Thread t1 = new Thread(() -> addEan(p,350))
//        Thread t2 = new Thread(() -> addEan(p,350))
        Thread t1 = addEan(p, 30);
        Thread t2 = addEan(p, 30);
        Thread t3 = addEan(p, 30);
        Thread t4 = addEan(p, 30);
        Thread t5 = addEan(p, 30);
        Thread t6 = addEan(p, 30);
        Thread t7 = addEan(p, 30);
        Thread t8 = addEan(p, 30);
        Thread t9 = addEan(p, 30);
        Thread t10 = addEan(p, 30);
        // tentando exceder o número de cores
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();
        t8.join();
        t9.join();
        t10.join();
        p.flush(true);

    }

    private Thread addEan(final PanamahStream p, final int j) {
        return new Thread() {
            public void run() {
                try {
                    int i = j;
                    while (i-- > 0) {
                        PanamahEan ean = new PanamahEan();
                        ean.setId("1");
                        ean.setProdutoId("1");
                        p.save(ean);
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
