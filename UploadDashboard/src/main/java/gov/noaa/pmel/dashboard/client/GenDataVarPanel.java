package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.GenDataVar;

import java.util.HashSet;

public class GenDataVarPanel extends FlagVarPanel {

    interface GenDataVarPanelUiBinder extends UiBinder<FlowPanel,GenDataVarPanel> {
    }

    private static final GenDataVarPanelUiBinder uiBinder = GWT.create(GenDataVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox accuracyValue;
    @UiField(provided = true)
    final LabeledTextBox precisionValue;
    @UiField(provided = true)
    final LabeledTextBox flagNameValue;

    /**
     * Creates a FlowPanel associated with the given GetDataVar metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param gendata
     *         associate this panel with this GetData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public GenDataVarPanel(GenDataVar gendata, HTML header, VariablesTabPanel parentPanel) {
        super(gendata, header, parentPanel);
        // Create the provided widgets added by this panel
        accuracyValue = new LabeledTextBox("Accuracy:", "11em", "20em", null, null);
        precisionValue = new LabeledTextBox("Precision:", "10em", "20em", null, null);
        //
        flagNameValue = new LabeledTextBox("QC column:", "10em", "20em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        GenDataVar gendata = (GenDataVar) vari;

        // Assign the values in the text fields added in this panel
        accuracyValue.setText((gendata.getAccuracy().getValueString()));
        // use the units for the variable and ignore the unit in the accuracy
        accuracyValue.setSuffix(gendata.getVarUnit());
        precisionValue.setText(gendata.getPrecision().getValueString());
        precisionValue.setSuffix(gendata.getVarUnit());
        flagNameValue.setText(gendata.getFlagColName());

        // Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)
        unitValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // For GenDataVar, change in the varible units also changes the accuracy and precision units
                String unit = unitValue.getText();
                accuracyValue.setSuffix(unit);
                precisionValue.setSuffix(unit);
                markInvalids(null);
            }
        });
        accuracyValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                GenDataVar gendata = (GenDataVar) vari;
                try {
                    gendata.setAccuracy(new NumericString(accuracyValue.getText(), unitValue.getText()));
                } catch ( IllegalArgumentException ex ) {
                    gendata.setAccuracy(null);
                }
                markInvalids(null);
            }
        });
        precisionValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                GenDataVar gendata = (GenDataVar) vari;
                try {
                    gendata.setPrecision(new NumericString(precisionValue.getText(), unitValue.getText()));
                } catch ( IllegalArgumentException ex ) {
                    gendata.setPrecision(null);
                }
                markInvalids(null);
            }
        });
        flagNameValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                GenDataVar gendata = (GenDataVar) vari;
                gendata.setFlagColName(flagNameValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((GenDataVar) vari).invalidFieldNames();

        // Appropriately mark the labels of fields added in this panel
        if ( invalids.contains("accuracy") )
            accuracyValue.markInvalid();
        else
            accuracyValue.markValid();

        if ( invalids.contains("precision") )
            precisionValue.markInvalid();
        else
            precisionValue.markValid();

        if ( invalids.contains("flagColName") )
            flagNameValue.markInvalid();
        else
            flagNameValue.markValid();

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
