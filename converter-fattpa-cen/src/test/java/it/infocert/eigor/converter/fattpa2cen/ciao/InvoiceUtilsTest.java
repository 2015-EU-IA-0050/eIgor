package it.infocert.eigor.converter.fattpa2cen.ciao;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0025InvoiceLine;
import org.junit.Test;
import org.reflections.Reflections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class InvoiceUtilsTest {

    @Test
    public void dodo() throws Exception {
        String s = "/BG0025/BG0026";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, new BG0000Invoice());
        assertThat(invoice.getBG0025InvoiceLine(), hasSize(1));
        assertThat(invoice.getBG0025InvoiceLine().get(0).getBG0026InvoiceLinePeriod(), hasSize(1));
    }

    @Test
    public void dodo2() throws Exception {
        String s = "/BG0025/BG0027";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, new BG0000Invoice());
        assertThat(invoice.getBG0025InvoiceLine(), hasSize(1));
        assertThat(invoice.getBG0025InvoiceLine().get(0).getBG0027InvoiceLineAllowances(), hasSize(1));
    }

    @Test
    public void failingDodo() throws Exception {
        String s = "/BG0025/BG0027/BG0026";
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        try {
            BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, new BG0000Invoice());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage().toLowerCase(), allOf(
                    containsString(s.toLowerCase()),
                    containsString("wrong")
            ));
        }
    }

    @Test
    public void doubleBG() throws Exception {
        String s = "/BG0025";

        BG0000Invoice inv = new BG0000Invoice();
        inv.getBG0025InvoiceLine().add(new BG0025InvoiceLine());
        inv.getBG0025InvoiceLine().add(new BG0025InvoiceLine());

        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        try {
            BG0000Invoice invoice = invoiceUtils.ensurePathExists(s, inv);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage().toLowerCase(), allOf(
                    containsString(s.toLowerCase()),
                    containsString("wrong"),
                    containsString("too many")
            ));
        }
    }
}