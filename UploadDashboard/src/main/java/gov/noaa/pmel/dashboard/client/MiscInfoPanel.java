package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.MiscInfo;

import java.util.HashSet;

public class MiscInfoPanel extends Composite {

    interface MiscInfoPanelUiBinder extends UiBinder<ScrollPanel,MiscInfoPanel> {
    }

    private static final MiscInfoPanelUiBinder uiBinder = GWT.create(MiscInfoPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField(provided = true)
    final LabeledTextBox nameValue;
    @UiField(provided = true)
    final LabeledTextBox sectionValue;
    @UiField(provided = true)
    final LabeledTextBox doiValue;
    @UiField(provided = true)
    final LabeledTextBox accessValue;
    @UiField(provided = true)
    final LabeledTextBox citationValue;
    @UiField(provided = true)
    final LabeledTextBox websiteValue;
    @UiField(provided = true)
    final LabeledTextBox downloadValue;
    @UiField(provided = true)
    final LabeledTextBox fundAgencyValue;
    @UiField(provided = true)
    final LabeledTextBox fundIdValue;
    @UiField(provided = true)
    final LabeledTextBox fundTitleValue;
    @UiField(provided = true)
    final LabeledTextBox projectValue;
    @UiField(provided = true)
    final LabeledTextArea synopsisValue;
    @UiField(provided = true)
    final LabeledTextArea purposeValue;
    @UiField(provided = true)
    final LabeledTextArea refsValue;
    @UiField(provided = true)
    final LabeledTextArea portsValue;
    @UiField(provided = true)
    final LabeledTextArea addnInfoValue;
    @UiField(provided = true)
    final LabeledTextArea historyValue;

    private final MiscInfo info;

    /**
     * Creates a FlowPanel associated with the given MiscInfo.
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param info
     *         associate this panel with this MiscInfo; cannot be null
     */
    public MiscInfoPanel(DashboardDataset dataset, MiscInfo info) {
        nameValue = new LabeledTextBox("Dataset name:", "11em", "20em", null, null);
        sectionValue = new LabeledTextBox("Section or leg name:", "10em", "20em", null, null);
        //
        doiValue = new LabeledTextBox("Dataset DOI:", "11em", "20em", null, null);
        accessValue = new LabeledTextBox("Accession ID:", "10em", "20em", null, null);
        //
        citationValue = new LabeledTextBox("Citation for this dataset:", "11em", "55.25em", null, null);
        //
        websiteValue = new LabeledTextBox("Website for this dataset:", "11em", "55.25em", null, null);
        //
        downloadValue = new LabeledTextBox("Dataset download URL:", "11em", "55.25em", null, null);
        //
        fundAgencyValue = new LabeledTextBox("Funding agency:", "11em", "55.25em", null, null);
        //
        fundIdValue = new LabeledTextBox("Funding ID:", "11em", "12em", null, null);
        fundTitleValue = new LabeledTextBox("Funding title:", "6em", "33em", null, null);
        //
        projectValue = new LabeledTextBox("Research project:", "11em", "55.25em", null, null);
        //
        synopsisValue = new LabeledTextArea("Synopsis of project", "5em", "54.5em");
        //
        purposeValue = new LabeledTextArea("Purpose of project", "10em", "54.5em");
        //
        refsValue = new LabeledTextArea("References", "10em", "54.5em");
        //
        portsValue = new LabeledTextArea("Ports of call", "5em", "54.5em");
        //
        addnInfoValue = new LabeledTextArea("Additional information", "10em", "54.5em");
        //
        historyValue = new LabeledTextArea("Archival history", "5em", "54.5em");

        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.MISC_INFO_TAB_TEXT + " for " + dataset.getDatasetId());

        this.info = info;

        // The following will assign the values in the labels and text fields
        getUpdatedMiscInfo();
    }

    @UiHandler("nameValue")
    void nameValueOnValueChange(ValueChangeEvent<String> event) {
        info.setDatasetName(nameValue.getText());
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = info.invalidFieldNames();

        if ( invalids.contains("datasetName") )
            nameValue.markInvalid();
        else
            nameValue.markValid();

        if ( invalids.contains("sectionName") )
            sectionValue.markInvalid();
        else
            sectionValue.markValid();

        if ( invalids.contains("datasetDoi") )
            doiValue.markInvalid();
        else
            doiValue.markValid();

        if ( invalids.contains("accessId") )
            accessValue.markInvalid();
        else
            accessValue.markValid();

        if ( invalids.contains("citation") )
            citationValue.markInvalid();
        else
            citationValue.markValid();

        if ( invalids.contains("website") )
            websiteValue.markInvalid();
        else
            websiteValue.markValid();

        if ( invalids.contains("downloadUrl") )
            downloadValue.markInvalid();
        else
            downloadValue.markValid();

        if ( invalids.contains("fundingAgency") )
            fundAgencyValue.markInvalid();
        else
            fundAgencyValue.markValid();

        if ( invalids.contains("fundingTitle") )
            fundTitleValue.markInvalid();
        else
            fundTitleValue.markValid();

        if ( invalids.contains("fundingId") )
            fundIdValue.markInvalid();
        else
            fundIdValue.markValid();

        if ( invalids.contains("researchProject") )
            projectValue.markInvalid();
        else
            projectValue.markValid();

        if ( invalids.contains("synopsis") )
            synopsisValue.markInvalid();
        else
            synopsisValue.markValid();

        if ( invalids.contains("purpose") )
            purposeValue.markInvalid();
        else
            purposeValue.markValid();

        if ( invalids.contains("references") )
            refsValue.markInvalid();
        else
            refsValue.markValid();

        if ( invalids.contains("portsOfCall") )
            portsValue.markInvalid();
        else
            portsValue.markValid();

        if ( invalids.contains("addnInfo") )
            addnInfoValue.markInvalid();
        else
            addnInfoValue.markValid();

        if ( invalids.contains("history") )
            historyValue.markInvalid();
        else
            historyValue.markValid();
    }


    /**
     * @return the updated MiscInfo; never null
     */
    public MiscInfo getUpdatedMiscInfo() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        nameValue.setText(info.getDatasetName());
        sectionValue.setText(info.getSectionName());
        doiValue.setText(info.getDatasetDoi());
        accessValue.setText(info.getAccessId());
        citationValue.setText(info.getCitation());
        websiteValue.setText(info.getWebsite());
        downloadValue.setText(info.getDownloadUrl());
        fundAgencyValue.setText(info.getFundingAgency());
        fundTitleValue.setText(info.getFundingTitle());
        fundIdValue.setText(info.getFundingId());
        projectValue.setText(info.getResearchProject());
        synopsisValue.setText(info.getSynopsis());
        purposeValue.setText(info.getPurpose());
        String value = "";
        for (String ref : info.getReferences()) {
            value += ref + "\n";
        }
        refsValue.setText(value.trim());
        value = "";
        for (String port : info.getPortsOfCall()) {
            value += port + "\n";
        }
        portsValue.setText(value.trim());
        value = "";
        for (String addInfo : info.getAddnInfo()) {
            value += addInfo + "\n";
        }
        addnInfoValue.setText(value.trim());
        value = "";
        for (Datestamp stamp : info.getHistory()) {
            value += stamp.fullOrPartialString() + "\n";
        }
        historyValue.setText(value.trim());

        markInvalids();

        return info;
    }

}
