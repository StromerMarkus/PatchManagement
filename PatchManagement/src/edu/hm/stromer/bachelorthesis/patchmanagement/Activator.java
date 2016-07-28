package edu.hm.stromer.bachelorthesis.patchmanagement;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Markus Stromer
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "PatchManagement"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	private Connector connector = new Connector();
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * Restore workspace to backup solution
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		connector.restore();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the shared connector
	 * 
	 * @return the shared connector
	 */
	public Connector getConnector() {
		return connector;
	}
	
	/**
	 * Writes given message to the error log console 
	 * 
	 * @param message errormessage
	 */
	public void writeErrorLog(String message) {
		writeErrorLog(message, null);	
	}

	/**
	 * Writes given message to the error log console
	 * 
	 * @param message errormessage
	 * @param e throwed exception
	 */
	public void writeErrorLog(String message, Throwable e) {
		ILog logging = getLog();
		Status status = new Status(Status.ERROR, PLUGIN_ID, message, e);
		logging.log(status);
	}
}
