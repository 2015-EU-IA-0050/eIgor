package it.infocert.eigor.converter.commons.ubl2cen;

import it.infocert.eigor.api.*;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.enums.VatExemptionReasonsCodes;
import it.infocert.eigor.model.core.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * The VAT Breakdown Custom Converter
 */
public class VATBreakdownConverter extends CustomConverterUtils implements CustomMapping<Document> {

    public static final Logger logger  =LoggerFactory.getLogger(VATBreakdownConverter.class);

    public ConversionResult<BG0000Invoice> toBG0023(Document document, BG0000Invoice invoice, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {

        BG0023VatBreakdown bg0023;

        Element rootElement = document.getRootElement();
        List<Namespace> namespacesInScope = rootElement.getNamespacesIntroduced();

        List<Element> taxSubtotals;
        Element taxTotal = findNamespaceChild(rootElement, namespacesInScope, "TaxTotal");

        if (taxTotal != null) {

            taxSubtotals = findNamespaceChildren(taxTotal, namespacesInScope, "TaxSubtotal");

            for (Element elemSub : taxSubtotals) {

                bg0023 = new BG0023VatBreakdown();

                Element taxableAmount = findNamespaceChild(elemSub, namespacesInScope, "TaxableAmount");
                if (taxableAmount != null) {
                    final String text = taxableAmount.getText();
                    try {
                        BT0116VatCategoryTaxableAmount bt0116 = new BT0116VatCategoryTaxableAmount(new BigDecimal(text));
                        bg0023.getBT0116VatCategoryTaxableAmount().add(bt0116);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element taxAmount = findNamespaceChild(elemSub, namespacesInScope, "TaxAmount");
                if (taxAmount != null) {
                    final String text = taxAmount.getText();
                    try {
                        BT0117VatCategoryTaxAmount bt0117 = new BT0117VatCategoryTaxAmount(new BigDecimal(text));
                        bg0023.getBT0117VatCategoryTaxAmount().add(bt0117);
                    } catch (NumberFormatException e) {
                        EigorRuntimeException ere = new EigorRuntimeException(
                                e,
                                ErrorMessage.builder()
                                        .message(e.getMessage())
                                        .location(callingLocation)
                                        .action(ErrorCode.Action.HARDCODED_MAP)
                                        .error(ErrorCode.Error.ILLEGAL_VALUE)
                                        .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                        .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                        .build());
                        errors.add(ConversionIssue.newError(ere));
                    }
                }

                Element taxCategory = findNamespaceChild(elemSub, namespacesInScope, "TaxCategory");
                if (taxCategory != null) {
                    Element id = findNamespaceChild(taxCategory, namespacesInScope, "ID");
                    if (id != null) {
                        final String text = id.getText();
                        try {
                            BT0118VatCategoryCode bt0118 = new BT0118VatCategoryCode(Untdid5305DutyTaxFeeCategories.valueOf(text));
                            bg0023.getBT0118VatCategoryCode().add(bt0118);
                        } catch (IllegalArgumentException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element percent = findNamespaceChild(taxCategory, namespacesInScope, "Percent");
                    if (percent != null) {
                        final String text = percent.getText();
                        try {
                            BT0119VatCategoryRate bt0119 = new BT0119VatCategoryRate(new BigDecimal(text));
                            bg0023.getBT0119VatCategoryRate().add(bt0119);
                        } catch (NumberFormatException e) {
                            EigorRuntimeException ere = new EigorRuntimeException(
                                    e,
                                    ErrorMessage.builder()
                                            .message(e.getMessage())
                                            .location(callingLocation)
                                            .action(ErrorCode.Action.HARDCODED_MAP)
                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
                                            .build());
                            errors.add(ConversionIssue.newError(ere));
                        }
                    }

                    Element taxExemptionReason = findNamespaceChild(taxCategory, namespacesInScope, "TaxExemptionReason");
                    if (taxExemptionReason != null) {
                        BT0120VatExemptionReasonText bt0120 = new BT0120VatExemptionReasonText(taxExemptionReason.getText());
                        bg0023.getBT0120VatExemptionReasonText().add(bt0120);
                    } else {
                        logger.debug("No TaxExemptionReason found");
                    }

                    Element taxExemptionReasonCode = findNamespaceChild(taxCategory, namespacesInScope, "TaxExemptionReasonCode");
                    if (taxExemptionReasonCode != null) {
                        final String text = taxExemptionReasonCode.getText();

                        BT0121VatExemptionReasonCode bt0121 = new BT0121VatExemptionReasonCode( text );
                        bg0023.getBT0121VatExemptionReasonCode().add(bt0121);

//                        #237
//                        try {
//                            BT0121VatExemptionReasonCode bt0121 = new BT0121VatExemptionReasonCode(VatExemptionReasonsCodes.valueOf(text));
//                            bg0023.getBT0121VatExemptionReasonCode().add(bt0121);
//                        } catch (IllegalArgumentException e) {
//                            EigorRuntimeException ere = new EigorRuntimeException(
//                                    e,
//                                    ErrorMessage.builder()
//                                            .message(e.getMessage())
//                                            .location(callingLocation)
//                                            .action(ErrorCode.Action.HARDCODED_MAP)
//                                            .error(ErrorCode.Error.ILLEGAL_VALUE)
//                                            .addParam(ErrorMessage.SOURCEMSG_PARAM, e.getMessage())
//                                            .addParam(ErrorMessage.OFFENDINGITEM_PARAM, text)
//                                            .build());
//                            errors.add(ConversionIssue.newError(ere));
//                        }
                    }

                    invoice.getBG0023VatBreakdown().add(bg0023);
                }
            }
        }
        return new ConversionResult<>(errors, invoice);
    }

    @Override
    public void map(BG0000Invoice cenInvoice, Document document, List<IConversionIssue> errors, ErrorCode.Location callingLocation) {
        toBG0023(document, cenInvoice, errors, callingLocation);
    }
}
