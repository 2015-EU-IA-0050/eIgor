package it.infocert.eigor.converter.cen2fattpa;

import com.google.common.collect.Lists;
import it.infocert.eigor.api.ConversionIssue;
import it.infocert.eigor.api.CustomMapping;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.converter.cen2fattpa.models.AllegatiType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaBodyType;
import it.infocert.eigor.converter.cen2fattpa.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.datatypes.Binary;
import it.infocert.eigor.model.core.model.AbstractBT;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BG0024AdditionalSupportingDocuments;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AttachmentConverter implements CustomMapping<FatturaElettronicaType> {

    private final static Logger log = LoggerFactory.getLogger(AttachmentConverter.class);

    private final List<String> cenPaths = Lists.newArrayList(
            "/BT0007",
            "/BT0010",
            "/BT0014",
            "/BT0018",
            "/BG0002/BT0023",
            "/BG0004/BT0028",
            "/BG0004/BT0033",
            "/BG0004/BT0034",
            "/BG0007/BT0045",
            "/BG0007/BT0047",
            "/BG0007/BG0009/BT0057",
            "/BG0007/BG0009/BT0058"
    );

    private final InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert.eigor"));


    @Override
    public void map(BG0000Invoice invoice, FatturaElettronicaType fatturaElettronica, List<IConversionIssue> errors) {
        List<FatturaElettronicaBodyType> bodies = fatturaElettronica.getFatturaElettronicaBody();
        int size = bodies.size();
        if (size > 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("Too many FatturaElettronicaBody found in current FatturaElettronica")));
        } else if (size < 1) {
            errors.add(ConversionIssue.newError(new IllegalArgumentException("No FatturaElettronicaBody found in current FatturaElettronica")));
        } else {
            FatturaElettronicaBodyType fatturaElettronicaBody = bodies.get(0);
            setUnmappedCenElements(invoice, fatturaElettronicaBody);
            forwardExistingAttachments(invoice, fatturaElettronicaBody);
        }
    }

    public String createAttachment(BG0000Invoice invoice) {
        StringBuilder sb = new StringBuilder();

        for (String path : cenPaths) {
            List<AbstractBT> foundBts = invoiceUtils.getBtRecursively(invoice, path, new ArrayList<AbstractBT>(0));
            for (AbstractBT bt : foundBts) {
                Object value = bt.getValue();
                sb.append(bt.denomination()).append(": ").append(value).append(System.lineSeparator());

            }
        }

        return sb.toString();
    }
    private void setUnmappedCenElements(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody) {

        String attachment = createAttachment(invoice);
        if (!"".equals(attachment)) {
            List<AllegatiType> allegati = fatturaElettronicaBody.getAllegati();
            AllegatiType allegato = new AllegatiType();
            allegato.setNomeAttachment("unmapped-cen-elements"); //TODO How to name it?
            allegato.setFormatoAttachment("txt");
            allegato.setAttachment(attachment.getBytes());
            allegati.add(allegato);
        }
    }

    private void forwardExistingAttachments(BG0000Invoice invoice, FatturaElettronicaBodyType fatturaElettronicaBody) {
        log.info("Starting converting Allegati");
        if (!invoice.getBG0024AdditionalSupportingDocuments().isEmpty()) {
            for (BG0024AdditionalSupportingDocuments documents : invoice.getBG0024AdditionalSupportingDocuments()) {
                AllegatiType allegati = new AllegatiType();
                allegati.setNomeAttachment(documents.getBT0122SupportingDocumentReference().get(0).getValue());

                if (!documents.getBT0123SupportingDocumentDescription().isEmpty()) {
                    allegati.setDescrizioneAttachment(documents.getBT0123SupportingDocumentDescription().get(0).getValue());
                }

                if (!documents.getBT0124ExternalDocumentLocation().isEmpty() && documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                    allegati.setAttachment(documents.getBT0124ExternalDocumentLocation().get(0).getValue().getBytes());
                } else if (documents.getBT0124ExternalDocumentLocation().isEmpty() && !documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().isEmpty()) {
                    Binary file = documents.getBT0125AttachedDocumentAndAttachedDocumentMimeCodeAndAttachedDocumentFilename().get(0).getValue();
                    allegati.setAttachment(file.getBytes());
                    allegati.setFormatoAttachment(file.getMimeType().toString());
                    allegati.setNomeAttachment(file.getFileName());
                }

                fatturaElettronicaBody.getAllegati().add(allegati);
            }
        }
    }
}