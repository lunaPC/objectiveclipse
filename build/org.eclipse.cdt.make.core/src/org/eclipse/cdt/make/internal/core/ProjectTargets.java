package org.eclipse.cdt.make.internal.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.eclipse.cdt.make.core.IMakeTarget;
import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProjectTargets {
	private static String BUILD_TARGET_ELEMENT = "buildTargets"; //$NON-NLS-1$
	private static String TARGET_ELEMENT = "target"; //$NON-NLS-1$

	private boolean isDirty;
	private HashMap targetMap = new HashMap();
	private IProject project;
	
	public ProjectTargets(IProject project) {
		this.project = project;
	}

	public ProjectTargets(IProject project, File targetFile) throws CoreException {
		Document document = null;
		try { 
			FileInputStream file = new FileInputStream(targetFile);
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(file);
		} catch (Exception e) {
			 throw new CoreException(new Status(IStatus.ERROR, MakeCorePlugin.getUniqueIdentifier(), -1, "Error reading target file", e));
		}
		Node node = document.getFirstChild();
		if (node.getNodeName().equals(BUILD_TARGET_ELEMENT)) {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				node = list.item(i);
				if (node.getNodeName().equals(TARGET_ELEMENT)) {
					IContainer container = null;
					NamedNodeMap attr = node.getAttributes();
					String path = attr.getNamedItem("targetID").getNodeValue();
					if (path != null) {
						container = project.getFolder(path);
					} else {
						container = project;
					}
					MakeTarget target = new MakeTarget(container, attr.getNamedItem("targetID").getNodeValue(), attr.getNamedItem("name").getNodeValue()); //$NON-NLS-1$ //$NON-NLS-2$
					String option = getString(node, "stopOnError");
					if (option != null) {
						target.setStopOnError(Boolean.valueOf(option).booleanValue());
					}
					option = getString(node, "useDefaultCommand");
					if (option != null) {
						target.setUseDefaultBuildCmd(Boolean.valueOf(option).booleanValue());
					}
					option = getString(node, "buildCommand");
					if (option != null) {
						target.setBuildCommand(new Path(option));
					}
					option = getString(node, "buildArguments");
					if (option != null) {
						target.setBuildArguments(option);
					}
					add(target);
				}
			}
		}
	}

	protected String getString(Node target, String tagName) {
		Node node = searchNode(target, tagName);
		return node != null ? (node.getFirstChild() == null ? null : node.getFirstChild().getNodeValue()) : null;
	}

	protected Node searchNode(Node target, String tagName) {
		NodeList list = target.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals(tagName))
				return list.item(i);
		}
		return null;
	}

	public IMakeTarget[] get(IContainer container) {
		ArrayList list = (ArrayList)targetMap.get(container);
		if (list != null) {
			return (IMakeTarget[])list.toArray(new IMakeTarget[list.size()]);
		}
		return new IMakeTarget[0];
	}
	
	public void add(MakeTarget target) throws CoreException {
		ArrayList list = (ArrayList)targetMap.get(target.getContainer());
		if (list != null && list.contains(target)) {
			throw new CoreException(new Status(IStatus.ERROR, MakeCorePlugin.getUniqueIdentifier(), -1, MakeCorePlugin.getResourceString("MakeTargetProvider.target_exists"), null)); //$NON-NLS-1$
		}
		if (list == null) {
			list = new ArrayList();
			targetMap.put(target.getContainer(), list);
		}
		list.add(target);
	}

	public boolean contains(MakeTarget target) {
		ArrayList list = (ArrayList)targetMap.get(target.getContainer());
		if (list != null && list.contains(target)) {
			return true;
		}
		return false;
	}

	public void remove(IMakeTarget target) {
		ArrayList list = (ArrayList)targetMap.get(target.getContainer());
		if (list != null && !list.contains(target)) {
			return;
		}
		list.remove(target);
		if (list.size() == 0) {
			targetMap.remove(list);
		}
	}

	public void setDirty() {
		isDirty = true;
	}

	public boolean isDirty() {
		return isDirty;		
	}

	public IProject getProject() {
		return project;
	}
	
	protected String getAsXML() throws IOException {
		Document doc = new DocumentImpl();
		Element configRootElement = doc.createElement(BUILD_TARGET_ELEMENT);
		doc.appendChild(configRootElement);
		return serializeDocument(doc);
	}
	
	protected String serializeDocument(Document doc) throws IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		OutputFormat format = new OutputFormat();
		format.setIndenting(true);
		format.setLineSeparator(System.getProperty("line.separator")); //$NON-NLS-1$
		Serializer serializer =
			SerializerFactory.getSerializerFactory(Method.XML).makeSerializer(new OutputStreamWriter(s, "UTF8"), format);
		serializer.asDOMSerializer().serialize(doc);
		return s.toString("UTF8"); //$NON-NLS-1$		
	}


}
