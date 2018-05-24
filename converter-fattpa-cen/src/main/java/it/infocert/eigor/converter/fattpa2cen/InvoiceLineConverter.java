package it.infocert.eigor.converter.fattpa2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.conversion.ConversionFailedException;
import it.infocert.eigor.api.conversion.converter.StringToUnitOfMeasureConverter;
import it.infocert.eigor.api.conversion.converter.TypeConverter;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.converter.fattpa2cen.converters.ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter;
import it.infocert.eigor.model.core.enums.UnitOfMeasureCodes;
import it.infocert.eigor.model.core.enums.Untdid5189ChargeAllowanceDescriptionCodes;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.Untdid7161SpecialServicesCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * The Invoice Line Custom Converter
 */
public class InvoiceLineConverter implements CustomMapping<Document> {

    public ConversionResult<BG0000Invoice> toBG0025(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        TypeConverter<String, Untdid5305DutyTaxFeeCategories> taxFeeCategoriesConverter = ItalianNaturaToUntdid5305DutyTaxFeeCategoriesConverter.newConverter();
        TypeConverter<String, UnitOfMeasureCodes> strToUnitOfMeasure = StringToUnitOfMeasureConverter.newConverter();


        BG0025InvoiceLine bg0025;

        Element rootElement = document.getRootElement();
        Element fatturaElettronicaBody = rootElement.getChild("FatturaElettronicaBody");

        if (fatturaElettronicaBody != null) {
            Element datiBeniServizi = fatturaElettronicaBody.getChild("DatiBeniServizi");
            if (datiBeniServizi != null) {
                List<Element> dettagliLinee = datiBeniServizi.getChildren();
                for (Element dettaglioLinee : dettagliLinee) {
                    if (dettaglioLinee.getName().equals("DettaglioLinee")) {

                        BigDecimal prezzoTotaleValue = null;
                        {
                            Element prezzoTotaleElement = dettaglioLinee.getChild("PrezzoTotale");
                            if (prezzoTotaleElement != null) {
                                try {
                                    prezzoTotaleValue = new BigDecimal(prezzoTotaleElement.getText());
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                        }

                        Untdid5305DutyTaxFeeCategories naturaValue = null;
                        {
                            Element naturaElement = dettaglioLinee.getChild("Natura");
                            if (naturaElement != null) {
                                try {
                                    naturaValue = taxFeeCategoriesConverter.convert(naturaElement.getText());
                                } catch (NullPointerException | ConversionFailedException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                        }

                        BigDecimal aliquotaIVAValue = null;
                        {
                            Element aliquotaIVAElement = dettaglioLinee.getChild("AliquotaIVA");
                            if (aliquotaIVAElement != null) {
                                try {
                                    aliquotaIVAValue = new BigDecimal(aliquotaIVAElement.getText());
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                        }

                        String descrizioneValue = null;
                        {
                            Element descrizioneElement = dettaglioLinee.getChild("Descrizione");
                            if (descrizioneElement != null) {
                                descrizioneValue = descrizioneElement.getValue();
                            }
                        }

                        Element tipoCessionePrestazione = dettaglioLinee.getChild("TipoCessionePrestazione");

                        if (tipoCessionePrestazione != null && prezzoTotaleValue != null) {
                            if (prezzoTotaleValue.signum() < 0 &&
                                    Arrays.asList("SC", "PR", "AB").contains(tipoCessionePrestazione.getText())) {

                                BG0020DocumentLevelAllowances bg0020 = new BG0020DocumentLevelAllowances();

                                BT0098DocumentLevelAllowanceReasonCode bt0098 = new BT0098DocumentLevelAllowanceReasonCode(Untdid5189ChargeAllowanceDescriptionCodes.Code95);
                                bg0020.getBT0098DocumentLevelAllowanceReasonCode().add(bt0098);

                                BT0092DocumentLevelAllowanceAmount bt0092 = new BT0092DocumentLevelAllowanceAmount(prezzoTotaleValue.negate());
                                bg0020.getBT0092DocumentLevelAllowanceAmount().add(bt0092);

                                if (naturaValue != null) {
                                    BT0095DocumentLevelAllowanceVatCategoryCode bt0095 = new BT0095DocumentLevelAllowanceVatCategoryCode(naturaValue);
                                    bg0020.getBT0095DocumentLevelAllowanceVatCategoryCode().add(bt0095);
                                }

                                if (aliquotaIVAValue != null) {
                                    BT0096DocumentLevelAllowanceVatRate bt0096 = new BT0096DocumentLevelAllowanceVatRate(aliquotaIVAValue);
                                    bg0020.getBT0096DocumentLevelAllowanceVatRate().add(bt0096);
                                }

                                if (descrizioneValue != null) {
                                    BT0097DocumentLevelAllowanceReason bt0097 = new BT0097DocumentLevelAllowanceReason(descrizioneValue);
                                    bg0020.getBT0097DocumentLevelAllowanceReason().add(bt0097);
                                }

                                invoice.getBG0020DocumentLevelAllowances().add(bg0020);
                            } else if (prezzoTotaleValue.signum() > 0 &&
                                    Arrays.asList("SC", "PR", "AB", "AC").contains(tipoCessionePrestazione.getText())) {

                                BG0021DocumentLevelCharges bg0021 = new BG0021DocumentLevelCharges();

                                BT0105DocumentLevelChargeReasonCode bt0105 = new BT0105DocumentLevelChargeReasonCode(Untdid7161SpecialServicesCodes.ABK);
                                bg0021.getBT0105DocumentLevelChargeReasonCode().add(bt0105);

                                BT0099DocumentLevelChargeAmount bt0099 = new BT0099DocumentLevelChargeAmount(prezzoTotaleValue);
                                bg0021.getBT0099DocumentLevelChargeAmount().add(bt0099);

                                if (naturaValue != null) {
                                    BT0102DocumentLevelChargeVatCategoryCode bt0102 = new BT0102DocumentLevelChargeVatCategoryCode(naturaValue);
                                    bg0021.getBT0102DocumentLevelChargeVatCategoryCode().add(bt0102);
                                }

                                if (aliquotaIVAValue != null) {
                                    BT0103DocumentLevelChargeVatRate bt0103 = new BT0103DocumentLevelChargeVatRate(aliquotaIVAValue);
                                    bg0021.getBT0103DocumentLevelChargeVatRate().add(bt0103);
                                }

                                if (descrizioneValue != null) {
                                    BT0104DocumentLevelChargeReason bt0104 = new BT0104DocumentLevelChargeReason(descrizioneValue);
                                    bg0021.getBT0104DocumentLevelChargeReason().add(bt0104);
                                }

                                invoice.getBG0021DocumentLevelCharges().add(bg0021);
                            }
                        } else {
                            bg0025 = new BG0025InvoiceLine();
                            Element numeroLinea = dettaglioLinee.getChild("NumeroLinea");
                            if (numeroLinea != null) {
                                BT0126InvoiceLineIdentifier invoiceLineIdentifier = new BT0126InvoiceLineIdentifier(numeroLinea.getText());
                                bg0025.getBT0126InvoiceLineIdentifier().add(invoiceLineIdentifier);
                            }
                            Element quantita = dettaglioLinee.getChild("Quantita");
                            if (quantita != null) {
                                try {
                                    BT0129InvoicedQuantity invoicedQuantity = new BT0129InvoicedQuantity(new BigDecimal(quantita.getText()));
                                    bg0025.getBT0129InvoicedQuantity().add(invoicedQuantity);
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            } else {
                                BT0129InvoicedQuantity invoicedQuantity = new BT0129InvoicedQuantity(BigDecimal.ONE);
                                bg0025.getBT0129InvoicedQuantity().add(invoicedQuantity);
                            }
                            Element unitaMisura = dettaglioLinee.getChild("UnitaMisura");
                            UnitOfMeasureCodes unitCode = null;
                            if (unitaMisura != null) {
                                final String text = unitaMisura.getText();
                                try {
                                    unitCode = strToUnitOfMeasure.convert(text);
                                    BT0130InvoicedQuantityUnitOfMeasureCode bt0130 = new BT0130InvoicedQuantityUnitOfMeasureCode(unitCode);
                                    bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(bt0130);
                                } catch (ConversionFailedException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message("Invalid UnitOfMeasureCodes: " + text)
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                    bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.C62_ONE));
                                }
                            } else {
                                bg0025.getBT0130InvoicedQuantityUnitOfMeasureCode().add(new BT0130InvoicedQuantityUnitOfMeasureCode(UnitOfMeasureCodes.C62_ONE));
                            }

                            if (prezzoTotaleValue != null) {
                                BT0131InvoiceLineNetAmount invoiceLineNetAmount = new BT0131InvoiceLineNetAmount(prezzoTotaleValue);
                                bg0025.getBT0131InvoiceLineNetAmount().add(invoiceLineNetAmount);
                            }

                            BG0029PriceDetails bg0029 = new BG0029PriceDetails();
                            Element prezzoUnitario = dettaglioLinee.getChild("PrezzoUnitario");
                            if (prezzoUnitario != null) {
                                try {
                                    BT0146ItemNetPrice itemNetPrice = new BT0146ItemNetPrice(new BigDecimal(prezzoUnitario.getText()));
                                    bg0029.getBT0146ItemNetPrice().add(itemNetPrice);
                                } catch (NumberFormatException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                            Element unitaMisuraDett = dettaglioLinee.getChild("UnitaMisura");
                            if (unitaMisuraDett != null) {
                                BT0149ItemPriceBaseQuantity itemPriceBaseQuantity = new BT0149ItemPriceBaseQuantity(BigDecimal.ONE);
                                bg0029.getBT0149ItemPriceBaseQuantity().add(itemPriceBaseQuantity);
                            }
                            if (unitCode != null) {
                                try {
                                    BT0150ItemPriceBaseQuantityUnitOfMeasureCode itemPriceBaseQuantityUnitOfMeasureCode = new BT0150ItemPriceBaseQuantityUnitOfMeasureCode(unitCode);
                                    bg0029.getBT0150ItemPriceBaseQuantityUnitOfMeasureCode().add(itemPriceBaseQuantityUnitOfMeasureCode);
                                } catch (NullPointerException e) {
                                    EigorRuntimeException ere = new EigorRuntimeException(e, ErrorMessage.builder().message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.MISSING_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .build());
                                    errors.add(ConversionIssue.newError(ere));
                                }
                            }
                            bg0025.getBG0029PriceDetails().add(bg0029);

                            BG0030LineVatInformation bg0030 = new BG0030LineVatInformation();
                            Untdid5305DutyTaxFeeCategories code = naturaValue != null ? naturaValue : Untdid5305DutyTaxFeeCategories.S;
                            BT0151InvoicedItemVatCategoryCode invoicedItemVatCategoryCode = new BT0151InvoicedItemVatCategoryCode(code);
                            bg0030.getBT0151InvoicedItemVatCategoryCode().add(invoicedItemVatCategoryCode);

                            if (aliquotaIVAValue != null) {
                                BT0152InvoicedItemVatRate invoicedItemVatRate = new BT0152InvoicedItemVatRate(aliquotaIVAValue);
                                bg0030.getBT0152InvoicedItemVatRate().add(invoicedItemVatRate);
                            }
                            bg0025.getBG0030LineVatInformation().add(bg0030);

                            BG0031ItemInformation bg0031 = new BG0031ItemInformation();
                            if (descrizioneValue != null) {
                                BT0153ItemName itemName = new BT0153ItemName(descrizioneValue);
                                bg0031.getBT0153ItemName().add(itemName);
                            }

                            BG0032ItemAttributes bg0032;
                            List<Element> altriDatiGestionaliList = dettaglioLinee.getChildren();
                            for (Element altriDatiGestionali : altriDatiGestionaliList) {
                                if (altriDatiGestionali.getName().equals("AltriDatiGestionali")) {
                                    bg0032 = new BG0032ItemAttributes();
                                    Element tipoDato = altriDatiGestionali.getChild("TipoDato");
                                    if (tipoDato != null) {
                                        BT0160ItemAttributeName itemAttributeName = new BT0160ItemAttributeName(tipoDato.getText());
                                        bg0032.getBT0160ItemAttributeName().add(itemAttributeName);
                                    }
                                    Element riferimentoTesto = altriDatiGestionali.getChild("RiferimentoTesto");
                                    if (riferimentoTesto != null) {
                                        BT0161ItemAttributeValue itemAttributeValue = new BT0161ItemAttributeValue(riferimentoTesto.getText());
                                        bg0032.getBT0161ItemAttributeValue().add(itemAttributeValue);
                                    }
                                    bg0031.getBG0032ItemAttributes().add(bg0032);
                                }
                            }

                            bg0025.getBG0031ItemInformation().add(bg0031);

                            invoice.getBG0025InvoiceLine().add(bg0025);
                        }
                    }
                }
            }
        }

        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0025(document, cenInvoice, errors, callingLocation);
    }
}
