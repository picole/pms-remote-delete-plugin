package de.pioneerdu.pms.plugins.remotedelete;

import java.io.*;

import java.util.List;
import java.util.LinkedList;
import java.lang.String;

import javax.swing.JComponent;

import org.apache.commons.io.*;

import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.RealFile;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.dlna.virtual.VirtualVideoAction;
import net.pms.external.AdditionalFolderAtRoot;
import net.pms.external.AdditionalResourceFolderListener;
import net.pms.external.StartStopListener;
import net.pms.PMS;

public class RemoteDelete implements StartStopListener, AdditionalResourceFolderListener, AdditionalFolderAtRoot {

	protected static List<String> markedForDeletion = new LinkedList<String>();
	protected static RemoteDeleteVirtualFolder rdRoot = new RemoteDeleteVirtualFolder("#- Trashcan -#");
	protected static Configuration rdConfig = new Configuration();

	public static List<String> getMarkedForDeletion() {
		return markedForDeletion;
	}

	public static void unMarkForDeletion(String value) {
		markedForDeletion.remove(value);
	}

	public static void markForDeletion(String value) {
		markedForDeletion.add(value);
		PMS.minimal(value + " marked for Deletion");
	}

	public void moveToTrash() {		
		
		
		for (int i = 0; i < markedForDeletion.size(); i++) {
			String cFileName = markedForDeletion.get(i);
			
			File cFile = new File(cFileName);
			String prefix = org.apache.commons.io.FilenameUtils.getPrefix(cFileName);
			String trashFolderPath = prefix + "PMS_Trashcan";
			if (cFile.exists()) {
				File cTrashDir = new File(trashFolderPath);
				cTrashDir.mkdir();
				if (cTrashDir.exists()) {
					try {
						if (cFile.isDirectory()) {
							FileUtils.moveDirectoryToDirectory(cFile, cTrashDir, false);
						} else {
							FileUtils.moveFileToDirectory(cFile, cTrashDir, false);
						}
						unMarkForDeletion(cFileName);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				rdRoot.getChildren().clear();
				getChild();
			}
		}
	}

	@Override
	public void donePlaying(DLNAMediaInfo media, DLNAResource resource) {

	}

	@Override
	public void nowPlaying(DLNAMediaInfo media, DLNAResource resource) {

	}

	@Override
	public void addAdditionalFolder(DLNAResource currentResource, DLNAResource child) {
		if (currentResource instanceof RemoteDeleteVirtualFolder || anyParentInstanceOf(currentResource, "RemoteDeleteVirtualFolder")) {
			return;
		}
		
		boolean found = false;
		for (DLNAResource resource : currentResource.getChildren()) {
			if (resource instanceof RemoteDeleteVirtualFolder) {
				found = true;
				break;
			}
		}
		if (!found) {
			VirtualFolder remoteDeleteFolder = new RemoteDeleteVirtualFolder("##- MARK FOR DELETION -##");
			currentResource.getChildren().add(remoteDeleteFolder);
			remoteDeleteFolder.setParent(currentResource);
			if (currentResource instanceof RealFile) {
				File fFile = ((RealFile) currentResource).getFile();
				
				final String fFilePath = fFile.getAbsolutePath();
				Boolean cMarked = getMarkedForDeletion().contains(fFilePath);
				String displayName = createFileDescription(fFile, cMarked);
				
				VirtualVideoAction deleteFolderAction = new VirtualVideoAction(
						displayName, cMarked) {

					@Override
					public boolean enable() {
						if (getMarkedForDeletion().contains(fFilePath)) {
							unMarkForDeletion(fFilePath);
							this.getParent().getChildren().clear();
							this.getParent().refreshChildren();
							rdRoot.getChildren().clear();
							
							getChild();
							return false;
						} else {
							markForDeletion(fFilePath);
							this.parent.refreshChildren();
							this.parent.getChildren().clear();
							this.parent.refreshChildren();
							rdRoot.getChildren().clear();
							getChild();
							return true;
						}
					}
				};
				remoteDeleteFolder.addChild(deleteFolderAction);
				
				if (cMarked == false) {
					File[] subFiles = fFile.listFiles();
					for (int i = 0; i < subFiles.length; i ++) {
						File iFile = subFiles[i];
						remoteDeleteFolder.addChild(createDeleteAction(iFile));
					}
					
				}
			}
		}
	}

	private boolean anyParentInstanceOf(DLNAResource currentResource, String className) {
		// TODO Auto-generated method stub
		return false;
	}

	private VirtualVideoAction createDeleteAction(File iFile) {
		final String fFilePath = iFile.getAbsolutePath();
		Boolean cMarked = getMarkedForDeletion().contains(fFilePath);
		String displayName = createFileDescription(iFile, cMarked);
		
		VirtualVideoAction deleteFolderAction = new VirtualVideoAction(
				displayName, cMarked) {

			@Override
			public boolean enable() {
				if (getMarkedForDeletion().contains(fFilePath)) {
					unMarkForDeletion(fFilePath);
					rdRoot.getChildren().clear();
					getChild();
					return true;
				} else {
					markForDeletion(fFilePath);
					rdRoot.getChildren().clear();
					getChild();				
					return true;
				}
				
				
				
			}
		};
		return deleteFolderAction;
	}

	private String createFileDescription(File fFile, Boolean marked) {
		String fDescription = "";
		if (fFile.isDirectory()) {
			fDescription = "[DIR] " + fFile.getName();
			FolderStatistics fStats = new FolderStatistics();
			long fSize = fStats.getFolderSize(fFile) / 1024 / 1024;
			int fiCount = fStats.getTotalFile();
			int foCount = fStats.getTotalFolder();
			fDescription += " [" + fSize + " MB | DIR:" + foCount + " | FIL: " + fiCount + "]";
		} else {
			fDescription = fFile.getName();
			fDescription += " [" + fFile.length() / 1024 / 1024 + " MB]";
		}
		return fDescription;
	}

	@Override
	public JComponent config() {
		// Returns a JComponent that is usually a JPanel
		// Is called via a button in the General Configuration tab
		return new ConfigurationUI();
	}

	@Override
	public String name() {
		// Returns the label on the button that is being used for rdConfig
		return "Remote Delete";
	}

	@Override
	public void shutdown() {
		// In case you need to close some network link or file handler
	}

	@Override
	public DLNAResource getChild() {
		// TODO Auto-generated method stub
		File cFile = null;
		for (int i = 0; i < getMarkedForDeletion().size(); i++) {
			cFile = new File(getMarkedForDeletion().get(i));
			rdRoot.addChild(new RealFile(cFile));
		}
		if (getMarkedForDeletion().size() > 0) {
			rdRoot.addChild(createEmptyTrash());
		}
		return rdRoot;
	}

	private VirtualVideoAction createEmptyTrash() {
		String displayName = "Move " + getMarkedForDeletion().size() + " items to the trash folder(s)";
		
		VirtualVideoAction emptyTrashAction = new VirtualVideoAction(displayName, false) {

			@Override
			public boolean enable() {
				moveToTrash();
				return true;
			}
		};
		return emptyTrashAction;
	}
}