/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/

package org.eclipse.cdt.internal.ui.cview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.IWorkingCopyManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class SelectionConverter {

	public static IStructuredSelection convertSelectionToCElements(ISelection s) {
		List converted = new ArrayList();
		if (s instanceof StructuredSelection) {
			Object[] elements = ((StructuredSelection) s).toArray();
			for (int i = 0; i < elements.length; i++) {
				Object e = elements[i];
				if (e instanceof ICElement) {
					converted.add(e);
				} else if (e instanceof IAdaptable) {
					ICElement c = (ICElement) ((IAdaptable) e).getAdapter(ICElement.class);
					if (c != null) {
						converted.add(c);
					}
				}
			}
		}
		return new StructuredSelection(converted.toArray());
	}

	public static IStructuredSelection convertSelectionToResources(ISelection s) {
		List converted = new ArrayList();
		if (s instanceof StructuredSelection) {
			Object[] elements = ((StructuredSelection) s).toArray();
			for (int i = 0; i < elements.length; i++) {
				Object e = elements[i];
				if (e instanceof IResource) {
					converted.add(e);
				} else if (e instanceof IAdaptable) {
					IResource r = (IResource) ((IAdaptable) e).getAdapter(IResource.class);
					if (r != null) {
						converted.add(r);
					}
				}
			}
		}
		return new StructuredSelection(converted.toArray());
	}

	public static boolean allResourcesAreOfType(IStructuredSelection selection, int resourceMask) {
		Iterator resources = selection.iterator();
		while (resources.hasNext()) {
			Object next = resources.next();
			if (next instanceof IAdaptable) {
				IAdaptable element = (IAdaptable) next;
				IResource resource = (IResource) element.getAdapter(IResource.class);

				if (resource == null) {
					return false;
				}
				if (!resourceIsType(resource, resourceMask)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the selection adapted to IResource. Returns null if any of the
	 * entries are not adaptable.
	 * 
	 * @param selection
	 *            the selection
	 * @param resourceMask
	 *            resource mask formed by bitwise OR of resource type constants
	 *            (defined on <code>IResource</code>)
	 * @return IStructuredSelection or null if any of the entries are not
	 *         adaptable.
	 * @see IResource#getType()
	 */
	public static IStructuredSelection allResources(IStructuredSelection selection, int resourceMask) {
		Iterator adaptables = selection.iterator();
		List result = new ArrayList();
		while (adaptables.hasNext()) {
			Object next = adaptables.next();
			if (next instanceof IAdaptable) {
				IResource resource = (IResource) ((IAdaptable) next).getAdapter(IResource.class);
				if (resource == null) {
					return null;
				} else if (resourceIsType(resource, resourceMask)) {
					result.add(resource);
				}
			} else {
				return null;
			}
		}
		return new StructuredSelection(result);

	}

	public static ICElement getElementAtOffset(ITextEditor editor) throws CModelException {
		return getElementAtOffset(getInput(editor), (ITextSelection) editor.getSelectionProvider().getSelection());
	}

	public static ICElement getElementAtOffset(ICElement input, ITextSelection selection) throws CModelException {
		if (input instanceof ITranslationUnit) {
			ITranslationUnit tunit = (ITranslationUnit) input;
			if (tunit.isWorkingCopy()) {
				synchronized (tunit) {
					if (tunit instanceof IWorkingCopy) {
						((IWorkingCopy) tunit).reconcile();
					}
				}
			}
			ICElement ref = tunit.getElementAtOffset(selection.getOffset());
			if (ref == null)
				return input;
			else
				return ref;
		}
		return null;
	}

	public static ICElement getInput(ITextEditor editor) {
		if (editor == null) return null;
		IEditorInput input = editor.getEditorInput();
		IWorkingCopyManager manager = CUIPlugin.getDefault().getWorkingCopyManager();
		return manager.getWorkingCopy(input);
	}

	/**
	 * Returns whether the type of the given resource is among the specified
	 * resource types.
	 * 
	 * @param resource
	 *            the resource
	 * @param resourceMask
	 *            resource mask formed by bitwise OR of resource type constants
	 *            (defined on <code>IResource</code>)
	 * @return <code>true</code> if the resources has a matching type, and
	 *         <code>false</code> otherwise
	 * @see IResource#getType()
	 */
	public static boolean resourceIsType(IResource resource, int resourceMask) {
		return (resource.getType() & resourceMask) != 0;
	}

}
