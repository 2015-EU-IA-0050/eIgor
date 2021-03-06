package it.infocert.eigor.model.core.model;

public interface Visitor {
    void startInvoice(BG0000Invoice invoice);

    void endInvoice(BG0000Invoice invoice);

    void startBTBG(BTBG invoiceNumber);

    void endBTBG(BTBG invoiceNumber);
}
