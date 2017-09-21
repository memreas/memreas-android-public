
package com.memreas.queue;

import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableTransfer;
import com.memreas.gallery.GalleryBean;
import com.memreas.gallery.GalleryBean.GalleryType;

public class MemreasTransferModel {

	public static enum MemreasQueueStatus {
		IN_PROGRESS, MOVE_TO_COMPLETED, COMPLETED, CANCELED, ERROR, PAUSED, PAUSED_AWS_SUCCESS, RESUMED, RESUMED_AWS_SUCCESS, TRANSFER_COMPLETED
	};

	public static enum Type {
		UPLOAD, DOWNLOAD, SYNC
	};

	private GalleryBean media;
	private String eventId="";
	private Type type;
	private boolean serverMedia;
	private MemreasQueueStatus status;
	private int progress;
	private PersistableTransfer persistableTransfer;

	public MemreasTransferModel(GalleryBean media) {
		this.media = media;
		if (media.getType() == GalleryType.NOT_SYNC) {
			type = Type.UPLOAD;
		} else if (media.getType() == GalleryType.SERVER) {
			type = Type.DOWNLOAD;
		} else {
			type = Type.SYNC;
		}
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public MemreasQueueStatus getMemreasQueueStatus() {
		return status;
	}

	public void setMemreasQueueStatus(MemreasQueueStatus status) {
		this.status = status;
	}

	public GalleryBean getMedia() {
		return media;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getName() {
		return media.getMediaName();
	}

	public PersistableTransfer getPersistableTransfer() {
		return persistableTransfer;
	}

	public void setPersistableTransfer(PersistableTransfer persistableTransfer) {
		this.persistableTransfer = persistableTransfer;
	}

	public boolean isServerMedia() {
		if (this.getMedia().getType() != GalleryType.NOT_SYNC) {
			return true;
		}
		return false;
	}

}
