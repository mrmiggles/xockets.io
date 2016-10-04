package com.tc.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class AttachUtils {
	
	private static final AttachUtils insta = new AttachUtils();
	
	public static AttachUtils insta(){
		return insta;
	}
	
	private AttachUtils(){}

	@SuppressWarnings("unchecked")
	public static void removeAll(Document doc) throws NotesException{
		boolean save = false;
		
		Vector<String> attachments = doc.getParentDatabase().getParent().evaluate("@AttachmentNames", doc);
		
		
		for(String attach : attachments){
			EmbeddedObject eo = doc.getAttachment(attach);
			if(eo!=null && eo.getType() == EmbeddedObject.EMBED_ATTACHMENT){
				eo.remove();
				save = true;
			}//end if
		}//end for
		
		//only save if we need to.
		if(save) {
			doc.save();
		}
	}
	
	public static void remove(Document doc, String attachmentName) throws NotesException{
		EmbeddedObject eo = doc.getAttachment(attachmentName);
		if(eo!=null){
			eo.remove();
			doc.save();
		}
	}
	
	public static void attach(byte[] byteMe, Document doc, String field, String ext) throws IOException, NotesException{
		File temp = File.createTempFile("tmp", ext);
		FileUtils.writeByteArrayToFile(temp, byteMe);
		attach(temp, doc, field);
	}
	
	public static void attach(InputStream in, Document doc, String field, String ext) throws IOException, NotesException{
		attach(IOUtils.toByteArray(in), doc, ext, field);
	}
	
	public static void attach(File file, Document doc, String field) throws IOException, NotesException{
		RichTextItem item = getRichText(doc, field);
		item.embedObject(EmbeddedObject.EMBED_ATTACHMENT, null, file.getAbsolutePath(), file.getName());
		doc.save();
		item.recycle();
		file.delete();
	}
	
	public static void attach(File file, Document doc, String field, String ext) throws IOException, NotesException{
		attach(file, doc, field);
	}
	
	public static RichTextItem getRichText(Document doc, String field) throws NotesException{
		RichTextItem rtitem = null;
		if(doc.hasItem(field)){
			rtitem = (RichTextItem) doc.getFirstItem(field);
		}else{
			rtitem = doc.createRichTextItem(field);
		}
		return rtitem;
		
	}


}
