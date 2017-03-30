package it.infocert.eigor.model.core.converter.fattpa2core.mapping;

import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BT0027SellerName;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class AnagraficaCedenteToBT27 {
    private final static String XPATHEXPRESSIONDENOM = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Denominazione";
    private final static String XPATHEXPRESSIONNOME = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Nome";
    private final static String XPATHEXPRESSIONCOGNOME = "//CedentePrestatore/DatiAnagrafici/Anagrafica/Cognome";


    public static void convertTo(Document doc, BG0000Invoice coreInvoice) {
        if (CommonConversionModule.hasNode(doc, XPATHEXPRESSIONDENOM)) {
            NodeList nodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONDENOM);
            String textContent = nodes.item(0).getTextContent();
            List<BT0027SellerName> sellerNames = new ArrayList<>();
            sellerNames.add(new BT0027SellerName(textContent));
            coreInvoice.getBG0004Seller().get(0).getBT0027SellerName().addAll(sellerNames);
        } else if (CommonConversionModule.hasNode(doc, XPATHEXPRESSIONNOME) && CommonConversionModule.hasNode(doc, XPATHEXPRESSIONCOGNOME)) {
            NodeList nameNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONNOME);
            String name = nameNodes.item(0).getTextContent();
            NodeList surnameNodes = CommonConversionModule.evaluateXpath(doc, XPATHEXPRESSIONCOGNOME);
            String surname = surnameNodes.item(0).getTextContent();
            List<BT0027SellerName> sellerNames = new ArrayList<>();
            sellerNames.add(new BT0027SellerName(name + " " + surname));
            coreInvoice.getBG0004Seller().get(0).getBT0027SellerName().addAll(sellerNames);
        }
    }
}