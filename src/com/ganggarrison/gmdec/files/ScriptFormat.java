/*
 * Copyright (C) 2010 Medo <smaxein@googlemail.com>
 * 
 * This file is part of GmkSplitter.
 * GmkSplitter is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */
package com.ganggarrison.gmdec.files;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lateralgm.file.GmFile;
import org.lateralgm.resources.Script;
import org.lateralgm.resources.Script.PScript;

import com.ganggarrison.gmdec.DeferredReferenceCreatorNotifier;
import com.ganggarrison.gmdec.FileTools;
import com.ganggarrison.gmdec.GmkSplitter;
import com.ganggarrison.gmdec.ResourceTreeEntry;
import com.ganggarrison.gmdec.GmkSplitter.IdPreservation;

public class ScriptFormat extends ResourceFormat<Script> {
	@Override
	public Script read(File path, ResourceTreeEntry entry, DeferredReferenceCreatorNotifier drcn) throws IOException {
		File scriptFile = new File(path, baseFilename(entry) + ".gml");
		Script script = new Script();
		script.setName(entry.name);

		StringBuilder code = new StringBuilder(FileTools.readFileAsString(scriptFile));

		Pattern pattern = Pattern.compile("/\\* !scriptId=(\\d+) \\*/\r\n");
		Matcher matcher = pattern.matcher(code);
		if (matcher.find()) {
			if (GmkSplitter.preserveIds == IdPreservation.ALL) {
				script.setId(Integer.valueOf(matcher.group(1)));
			}
			code.delete(matcher.start(), matcher.end());
		}
		script.put(PScript.CODE, code.toString());

		return script;
	}

	@Override
	public void write(File path, Script script, GmFile gmf) throws IOException {
		File scriptFile = new File(path, baseFilename(script) + ".gml");
		StringBuilder code = new StringBuilder(script.getCode());
		if (GmkSplitter.preserveIds == IdPreservation.ALL) {
			code.insert(0, "/* !scriptId=" + script.getId() + " */\r\n");
		}
		FileTools.writeFile(scriptFile, code.toString());
	}
}
