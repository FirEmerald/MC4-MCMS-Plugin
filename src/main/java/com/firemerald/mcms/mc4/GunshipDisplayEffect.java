package com.firemerald.mcms.mc4;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.IModelHolder;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.BoneEffect;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.ResourceLocation;

public class GunshipDisplayEffect extends BoneEffect
{
	public static final ResourceLocation DISPLAY_TEX = new ResourceLocation(MC4Plugin.ID, "display.png");
	public String id;
	public Material texture;

	public GunshipDisplayEffect(String name, @Nullable RenderBone<?> parent)
	{
		this(name, parent, null, "undefined");
	}
	
	public GunshipDisplayEffect(String name, @Nullable RenderBone<?> parent, Material texture, String id)
	{
		super(name, parent);
		this.texture = texture;
		this.id = id;
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return DISPLAY_TEX;
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}

	@Override
	public void doPreRender(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex)
	{
		if (texture == null) Main.instance.textureManager.unbindTexture();
		else Main.instance.currentModelShader.bindMaterial(texture);
	}

	@Override
	public void doPostRenderBone(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex) {}

	@Override
	public void doPostRenderChildren(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex)
	{
		defaultTex.run();
	}

	@Override
	public String getXMLName()
	{
		return MC4Plugin.ID + ":display";
	}

	@Override
	public void doCleanUp() {}

	@Override
	public BoneEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new GunshipDisplayEffect(name, clonedParent, texture, id);
	}
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		id = el.getString("id", "undefined");
		if (el.hasAttribute("texture")) texture = Main.instance.project.getTexture(el.getString("texture", "should not happen"));
		else texture = null;
	}
	
	@Override
	public void saveToXML(AbstractElement el, float scale)
	{
		super.saveToXML(el, scale);
		el.setString("id", id);
		if (texture != null) el.setString("texture", Main.instance.project.getTextureName(texture));
	}
	
	private ComponentFloatingLabel labelTexture;
	private SelectorButton textureSelector;
	private ComponentFloatingLabel labelID;
	private ComponentText idT;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editorY = super.onSelect(editorPanes, editorY);

		editor.addElement(labelTexture = new ComponentFloatingLabel(editorX, editorY, editorX + 53, editorY + 20, Main.instance.fontMsg, "Texture"));
		editor.addElement(textureSelector = new SelectorButton(editorX + 53, editorY, editorX + 300, editorY + 20, Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : texture == null ? "no texture selected" : Main.instance.project.getTextureName(texture), MiscUtil.array("none", Main.instance.project.getTextureNames()), (ind, value) -> {
			//Main.instance.project.onAction(); TODO undo?
			if (ind == 0)
			{
				GunshipDisplayEffect.this.texture = null;
				return "no texture selected";
			}
			else
			{
				GunshipDisplayEffect.this.texture = Main.instance.project.getTexture(value);
				return value;
			}
		}));
		editorY += 20;
		editor.addElement(labelID = new ComponentFloatingLabel(editorX, editorY, editorX + 17, editorY + 20, Main.instance.fontMsg, "ID"));
		editor.addElement(idT = new ComponentText(editorX + 17, editorY, editorX + 300, editorY + 20, Main.instance.fontMsg, this.id, txt -> this.id = txt));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelTexture);
		editor.removeElement(textureSelector);
		editor.removeElement(labelID);
		editor.removeElement(idT);
		labelTexture    = null;
		textureSelector = null;
		labelID         = null;
		idT             = null;
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.TEXTURE)
		{
			Project project = Main.instance.project;
			String texName = project.getTextureName(texture);
			if (texName == null) texture = null;
			if (textureSelector != null)
			{
				textureSelector.setValues(MiscUtil.array("none", project.getTextureNames()));
				textureSelector.setText(Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : texName == null ? "no texture selected" : texName);
			}
		}
	}
	
	@Override
	public @Nullable Material getTexture(Material prev)
	{
		return texture;
	}
}