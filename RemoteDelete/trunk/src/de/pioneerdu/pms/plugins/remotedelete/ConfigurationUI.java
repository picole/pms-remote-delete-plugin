package de.pioneerdu.pms.plugins.remotedelete;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.ComponentOrientation;
import java.awt.Component;
import javax.swing.JScrollPane;

import java.awt.Cursor;


public class ConfigurationUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5302295122093582195L;
	private JTable table;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public ConfigurationUI() {
		Properties rdConfig = RemoteDelete.rdConfig.getConfiguration();
		
		setSize(new Dimension(600, 400));
		setPreferredSize(new Dimension(600, 400));
		setMinimumSize(new Dimension(600, 400));
		setMaximumSize(new Dimension(600, 400));
		setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 600, 400);
		add(tabbedPane);
		
		JPanel pConfig = new JPanel();
		tabbedPane.addTab("Configuration", null, pConfig, null);
		tabbedPane.setEnabledAt(0, true);
		pConfig.setLayout(null);
		
		table = new JTable();

		DefaultTableModel disallowKeyEditModel = new DefaultTableModel(rdConfig.values().size(), 2) {
		    public boolean isCellEditable(int rowIndex, int mColIndex) {
		        return (mColIndex == 0) ? false : true;
		    }
		};
		    
		table.setModel(disallowKeyEditModel);
		Enumeration<Object> keyEnumerator = rdConfig.keys();
		int cRow = 0;
		while (keyEnumerator.hasMoreElements()) {
			String cKey = keyEnumerator.nextElement().toString();
			table.getModel().setValueAt(cKey, cRow, 0);
			table.getModel().setValueAt(rdConfig.getProperty(cKey), cRow, 1);
			cRow ++;
		}
		
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.setBounds(10, 11, 575, 319);
		pConfig.add(table);
		
		JButton btnSaveConfiguration = new JButton("Save Configuration");
		btnSaveConfiguration.setBounds(10, 341, 127, 23);
		pConfig.add(btnSaveConfiguration);
		
		JPanel pStatus = new JPanel();
		tabbedPane.addTab("Status", null, pStatus, null);
		tabbedPane.setEnabledAt(1, false);
		pStatus.setLayout(null);
		
		JPanel pHelp = new JPanel();
		tabbedPane.addTab("Help", null, pHelp, null);
		tabbedPane.setEnabledAt(2, true);
		pHelp.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		scrollPane.setBounds(10, 11, 575, 353);
		
		pHelp.add(scrollPane);
		
		JTextPane txtrThoughThisPlugin = new JTextPane();
		scrollPane.setViewportView(txtrThoughThisPlugin);
		txtrThoughThisPlugin.setAlignmentY(Component.TOP_ALIGNMENT);
		txtrThoughThisPlugin.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtrThoughThisPlugin.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		txtrThoughThisPlugin.setFont(new Font("SansSerif", Font.PLAIN, 11));
		txtrThoughThisPlugin.setText("Though this plugin is called RemoteDelete it will actually NEVER DELETE any files or folders from your media server. The reasons why this would be to dangerous have been discussed on the official forum.\r\n\r\nInstead it allows you to use your Media Renderer (i.e. PS3) to mark files and folders for \"deletion\". In the RemoteDelete virtual folder in your library you will be able to empty the trash which will still NOT DELETE but move the \"deleted\" files and folders to a folder outside the scope of PMS.\r\n\r\nSo these are the Steps to happy deletion :-)\r\n\r\n1.) Browse your Media Files and mark things for deletion\r\n2.) Empty The Trashcan (using your Media Renderer) which will move the files to a folder outside the PMS library (By Default it is <Root Of Drive>/PMS_Trashcan)\r\n3.) Manually empty the Trash Folder from time to time or use a task scheduler / cronjob to have it done automatically\r\n\r\nConfiguration options explained:\r\n\r\nrdLogEnabled (true/false, default true) & rdLogPath  (default plugins/RemoteDelete.log):\r\nrdLogEnabled enables the log output of RemoteDelete. rdLogPath is the path to the logfile. If you do not specify an absolute path the log will be written relatively to the PMS program folder.\r\n\r\n\r\nrdAsyncMoveQueueInterval (Interval in seconds, default 60):\r\nNormally the movement of the files should be very fast since RemoteDelete maintains (tries to maintain) a trashcan folder for each partition. But sometimes the media server itself locks files or directories while maintaining its library. As long as thos are locked the files cannot be moved. Therefore RemoteDelete maintains a queue of items to be moved to the trash folder and retries to move previously locked items at the given interval. \r\n\r\n\r\nrdDeleteFolder (One relative path  OR comma separated list of absolute paths, default PMS_Trashcan):\r\nRemoteDelete tries to quickly move deleted items to the specified trash folder. This normally succeeds if source and destination reside on the same physical harddrive. From within Java it is not easy to determine the boundaries of physical harddrives on every OS.\r\n\r\nOn Windows you should be fine with the defaults. RemoteDelete will move the files to the Root of the Drive (i.e. C:\\PMS_Trashcan, D:\\PMS_Trashcan, ...)\r\nOn Linux the defaults are probably not that good because the trashcan will be /PMS_Trashcan by default. You should define rdDeleteFolder with a list of the mount points of your physical harddrives containing media (i.e. rdDeleteFolder=/var/media/PMS_Trashcan,/home/peter_paul_and_mary/PMS_Trashcan. RemoteDelete will automatically chose the best folder for each moved file or folder from the list.\r\nMac OS? Hell be a man and buy yourself a computer. These glossy and shiny things don't do anything well. \r\n\r\n\r\nrdKeepAsyncMoveQueueOnDisc, rdKeepTrashcanOnDisc (true / false, default true):\r\nIf true RemoteDelete will rescue remember in the virtual trashcan folder and move queue after a crash or restart of PMS. Otherwise it will keep these lists only in RAM.\r\n\r\n\r\nrdKeepFolderStructureInTrashcan (true/false, default true):\r\nSpecfies if RemoteDelete should move files and folders with their complete path. \r\nExample: Sourcefile is D:\\Videos\\Movies\\Terminator\\Terminator.mkv\r\nDestination if true: D:\\PMS_Trashcan\\Videos\\Movies\\Terminator\\Terminator.mkv\r\nDestination if false: D:\\PMS_Trashcan\\Terminator.mkv\r\n");
		txtrThoughThisPlugin.setEditable(false);
		txtrThoughThisPlugin.setSelectionStart(0);
		txtrThoughThisPlugin.setSelectionEnd(0);

	}
}
