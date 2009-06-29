package gov.noaa.pmel.tmap.las.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

interface RPCServiceAsync {
	public void getCategories(String id, AsyncCallback categoryCallback);
	public void getVariables(String id, AsyncCallback variablesCallback);
    public void getGrid(String dsid, String varid, AsyncCallback gridCallback);
    public void getOperations(String view, String dsid, String varid, AsyncCallback opCallback);
	public void getOptions(String opid, AsyncCallback optionsCallback);
    public void getTimeSeries(AsyncCallback timeSeriesCallback);
}
