package it.infocert.eigor.model.core.converter.pa2core.mapping;

import it.infocert.eigor.model.core.model.BG0004Sellers;
import it.infocert.eigor.model.core.model.CoreInvoice;
import org.w3c.dom.Document;

public class CedentePrestatoreToBG04 {

    public static void convertTo(Document doc, CoreInvoice coreInvoice) {
        BG0004Sellers bg04Seller = new BG0004Sellers();
        coreInvoice.getBg0004Sellers().add(bg04Seller);
        AnagraficaCedenteToBT27.convertTo(doc, coreInvoice);
        CodiceFiscaleCedenteToBT29.convertTo(doc, coreInvoice);
        IdFiscaleIvaToBT31.convertTo(doc, coreInvoice);
        RegimeFiscaleToBT32.convertTo(doc, coreInvoice);
    }
}
