package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.model.core.datatypes.Identifier;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * The Buyer Electronic Address Custom Converter
 */
public class BuyerConverter implements CustomMapping<Document> {

    private static final String pec = "IT:PEC";
    private static final String ipa = "IT:IPA";
    private static final String cf = "IT:CF";
    private static final String eori = "IT:EORI";

    public ConversionResult<BG0000Invoice> toBG0007(Document document, BG0000Invoice invoice, List<IConversionIssue> errors) {

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaHeader = rootElement.getChild("FatturaElettronicaHeader");

        if (fatturaElettronicaHeader != null) {
            Element cessionarioCommittente = fatturaElettronicaHeader.getChild("CessionarioCommittente");
            if (cessionarioCommittente != null) {
                String nazioneStr = "";
                Element sede = cessionarioCommittente.getChild("Sede");
                if (sede != null) {
                    Element nazione = sede.getChild("Nazione");
                    if (nazione != null) {
                        nazioneStr = nazione.getText();
                    }
                }

                Element datiAnagrafici = cessionarioCommittente.getChild("DatiAnagrafici");
                if (datiAnagrafici != null) {
                    Element codiceFiscale = datiAnagrafici.getChild("CodiceFiscale");
                    if (codiceFiscale != null) {
                        BT0046BuyerIdentifierAndSchemeIdentifier bt0046BuyerIdentifierAndSchemeIdentifier;
                        if (nazioneStr.equals("IT")) {
                            bt0046BuyerIdentifierAndSchemeIdentifier = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(cf, codiceFiscale.getText()));
                        }else {
                            bt0046BuyerIdentifierAndSchemeIdentifier = new BT0046BuyerIdentifierAndSchemeIdentifier(new Identifier(codiceFiscale.getText()));
                        }
                        if (invoice.getBG0007Buyer().isEmpty()) {
                            invoice.getBG0007Buyer().add(new BG0007Buyer());
                        }
                        invoice.getBG0007Buyer(0).getBT0046BuyerIdentifierAndSchemeIdentifier().add(bt0046BuyerIdentifierAndSchemeIdentifier);
                    }

                    Element anagrafica = datiAnagrafici.getChild("Anagrafica");
                    if (anagrafica != null) {
                        Element codEORI = anagrafica.getChild("CodEORI");
                        if (codEORI != null && codiceFiscale != null) {
                            BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier bt0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier = null;
                            if (nazioneStr.equals("IT")) {
                                bt0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier = new BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(eori, codiceFiscale.getText()));
                            }else {
                                bt0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier = new BT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier(new Identifier(codiceFiscale.getText()));
                            }
                            if (invoice.getBG0007Buyer().isEmpty()) {
                                invoice.getBG0007Buyer().add(new BG0007Buyer());
                            }
                            invoice.getBG0007Buyer(0).getBT0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier().add(bt0047BuyerLegalRegistrationIdentifierAndSchemeIdentifier);
                        }
                    }
                }
            }

            Element datiTrasmissione = fatturaElettronicaHeader.getChild("DatiTrasmissione");
            if (datiTrasmissione != null) {
                Element codiceDestinatario = datiTrasmissione.getChild("CodiceDestinatario");
                Element pecDestinatario = datiTrasmissione.getChild("PECDestinatario");
                if (codiceDestinatario != null) {
                    BT0049BuyerElectronicAddressAndSchemeIdentifier buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(ipa, codiceDestinatario.getText()));
                    if (invoice.getBG0007Buyer().isEmpty()) {
                        invoice.getBG0007Buyer().add(new BG0007Buyer());
                    }
                    invoice.getBG0007Buyer(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier().add(buyerElectronicAddressAndSchemeIdentifier);
                } else if (pecDestinatario != null) {
                    BT0049BuyerElectronicAddressAndSchemeIdentifier buyerElectronicAddressAndSchemeIdentifier = new BT0049BuyerElectronicAddressAndSchemeIdentifier(new Identifier(pec, pecDestinatario.getText()));
                    if (invoice.getBG0007Buyer().isEmpty()) {
                        invoice.getBG0007Buyer().add(new BG0007Buyer());
                    }
                    invoice.getBG0007Buyer(0).getBT0049BuyerElectronicAddressAndSchemeIdentifier().add(buyerElectronicAddressAndSchemeIdentifier);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0007(document, cenInvoice, errors);
    }
}