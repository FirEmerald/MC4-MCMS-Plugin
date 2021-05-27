package com.firemerald.mcms.mc4;

import org.eclipse.jdt.annotation.Nullable;
import org.joml.Matrix4d;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.IModelHolder;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.BoneEffect;
import firemerald.mcms.api.model.effects.EffectRenderStage;
import firemerald.mcms.api.model.effects.ItemRenderEffect;
import firemerald.mcms.api.model.effects.StagedPosedBoneEffect;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.shader.ModelShaderBase;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TransformType;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.ModelMesh;

public class AssemblyTerminalEffect extends StagedPosedBoneEffect
{
	public static final ResourceLocation DISPLAY_TEX = new ResourceLocation(MC4Plugin.ID, "assembly.png");
	public static final ResourceLocation TEXTURE = new ResourceLocation(MC4Plugin.ID, "assembly_terminal.png");
	public static final ModelMesh MESH = new ModelMesh(new float[] {
			-80, 0, -67,
			-80, 0,  67,
			 80, 0,  67,
			 80, 0, -67
	}, new float[] {
			0, 0,
			0, 1,
			1, 1,
			1, 0
	}, new float[] {
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0
	}, new int[] {
			0, 1, 3,
			3, 1, 2
	}, DrawMode.TRIANGLES);
	protected float scale = 1;

	public AssemblyTerminalEffect(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage)
	{
		this(name, parent, stage, 1);
	}

	public AssemblyTerminalEffect(String name, @Nullable RenderBone<?> parent, EffectRenderStage stage, float scale)
	{
		super(name, parent, stage);
		this.scale = scale;
	}

	public AssemblyTerminalEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage)
	{
		this(name, parent, transform, stage, 1);
	}

	public AssemblyTerminalEffect(String name, @Nullable RenderBone<?> parent, Transformation transform, EffectRenderStage stage, float scale)
	{
		super(name, parent, transform, stage);
		this.scale = scale;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}

	@Override
	public EffectRenderStage getDefaultStage()
	{
		return EffectRenderStage.POST_CHILDREN;
	}

	@Override
	public ResourceLocation getDisplayIcon()
	{
		return DISPLAY_TEX;
	}

	private ComponentFloatingLabel labelScale;
	private ComponentTextFloat scaleXT;
	private ComponentIncrementFloat scaleXP, scaleXS;
	
	@Override
	public int onSelect(EditorPanes editorPanes, int editorY)
	{
		editorY = super.onSelect(editorPanes, editorY);
		GuiElementContainer editor = editorPanes.editor.container;
		int editorX = editorPanes.editor.minX;
		editor.addElement(labelScale = new ComponentFloatingLabel( editorX      , editorY, editorX + 300, editorY + 20 , Main.instance.fontMsg, "Scale"));
		editorY += 20;
		editor.addElement(scaleXT    = new ComponentTextFloat(     editorX      , editorY, editorX + 290 , editorY + 20 , Main.instance.fontMsg, getScale(), Float.MIN_VALUE, Float.POSITIVE_INFINITY, this::setScale));
		editor.addElement(scaleXP    = new ComponentIncrementFloat(editorX + 290 , editorY                              , scaleXT, 0.0625f));
		editor.addElement(scaleXS    = new ComponentIncrementFloat(editorX + 290 , editorY + 10                         , scaleXT, -0.0625f));
		editorY += 20;
		return editorY;
	}

	@Override
	public void onDeselect(EditorPanes editorPanes)
	{
		super.onDeselect(editorPanes);
		GuiElementContainer editor = editorPanes.editor.container;
		editor.removeElement(labelScale);
		editor.removeElement(scaleXT);
		editor.removeElement(scaleXP);
		editor.removeElement(scaleXS);
		labelScale = null;
		scaleXT    = null;
		scaleXP    = null;
		scaleXS    = null;
	}

	@Override
	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> iRigged)
	{
		if (newParent instanceof RenderBone<?>) return cloneObject((RenderBone<?>) newParent);
		else return null;
	}
	
	@Override
	public void render(IModelHolder holder, Matrix4d currentTransform, Runnable defaultTex)
	{
		ModelShaderBase.MODEL.push();
		ModelShaderBase.MODEL.matrix().scale(scale / Main.instance.project.getScale());
		Main.instance.currentModelShader.updateModel();
		Main.instance.textureManager.bindTexture(TEXTURE);
		MESH.render();
		for (float x = -9f / 8f; x <= 9f / 8f; x += 9f / 8f) for (float z = -9f / 8f; z <= 9f / 8f; z += 9f / 8f)
		{
			ModelShaderBase.MODEL.push();
			ModelShaderBase.MODEL.matrix().scale(16);
			ModelShaderBase.MODEL.matrix().translate(x, 1f / 32f, z);
			ModelShaderBase.MODEL.matrix().rotateX(-Math.PI / 2);
			ModelShaderBase.MODEL.matrix().rotateY(Math.PI);
			ModelShaderBase.MODEL.matrix().mul(TransformType.FIXED.matrix());
			Main.instance.currentModelShader.updateModel();
			Main.instance.textureManager.bindTexture(ItemRenderEffect.TEX);
			RenderUtil.ITEM_MESH.render();
			ModelShaderBase.MODEL.pop();
		}
		ModelShaderBase.MODEL.pop();
		//TODO render
		defaultTex.run();
	}
	
	@Override
	public void loadFromXML(AbstractElement el, float scale)
	{
		super.loadFromXML(el, scale);
		this.scale = el.getFloat("scale", 1f);
	}
	
	@Override
	public void saveToXML(AbstractElement el, float scale)
	{
		super.saveToXML(el, scale);
		if (this.scale != 1f) el.setFloat("scale", this.scale);
	}

	@Override
	public String getXMLName()
	{
		return MC4Plugin.ID + ":assembly_terminal";
	}

	@Override
	public void doCleanUp() {}

	@Override
	public BoneEffect cloneObject(RenderBone<?> clonedParent)
	{
		return new AssemblyTerminalEffect(name, clonedParent, transform, stage, scale);
	}
}