package com.firemerald.mcms.mc4;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.SelectorButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupGunshipDisplayEffect<T extends RenderBone<T>> extends GuiPopup
{
	public final T parent;
	public final DecoPane pane;
	private ComponentText name;
	private ComponentFloatingLabel labelTexture;
	private SelectorButton textureSelector;
	private ComponentFloatingLabel labelID;
	private ComponentText idT;
	public final StandardButton ok, cancel;
	public Material texture;
	
	public GuiPopupGunshipDisplayEffect(T parent)
	{
		this.parent = parent;
		final int cw = 240;
		final int ch = 100;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, "display", name -> {}) {
			@Override
			public boolean shouldUndo()
			{
				return false;
			}
		});
		y += 20;
		this.addElement(labelTexture = new ComponentFloatingLabel(cx, y, cx + 53, y + 20, Main.instance.fontMsg, "Texture"));
		this.addElement(textureSelector = new SelectorButton(cx + 53, y, cx + cy, y + 20, Main.instance.project.getTextureNames().isEmpty() ? "no textures available" : Main.instance.project.getTextureName() == null ? "no texture selected" : Main.instance.project.getTextureName(), MiscUtil.array("none", Main.instance.project.getTextureNames()), (ind, value) -> {
			//Main.instance.project.onAction(); TODO undo?
			if (ind == 0)
			{
				GuiPopupGunshipDisplayEffect.this.texture = null;
				return "no texture selected";
			}
			else
			{
				GuiPopupGunshipDisplayEffect.this.texture = Main.instance.project.getTexture(value);
				return value;
			}
		}));
		y += 20;
		this.addElement(labelID = new ComponentFloatingLabel(cx, y, cx + 17, y + 20, Main.instance.fontMsg, "ID"));
		this.addElement(idT = new ComponentText(cx + 17, y, cx + cw, y + 20, Main.instance.fontMsg, "undefined", txt -> {}){
			@Override
			public boolean shouldUndo()
			{
				return false;
			}
		});
		
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 240;
		final int ch = 100;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, y, cx + cw, y + 20);
		y += 20;
		labelTexture.setSize(cx, y, cx + 53, y + 20);
		textureSelector.setSize(cx + 53, y, cx + cw, y + 20);
		y += 20;
		labelID.setSize(cx, y, cx + 17, y + 20);
		idT.setSize(cx + 17, y, cx + cw, y + 20);
		
		ok.setSize(cx, cy + ch - 20, cx + 46, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.currentModelShader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.currentModelShader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		deactivate();
		final GunshipDisplayEffect newBone = new GunshipDisplayEffect(name.getText(), parent, texture, idT.getText());
		final T parent = this.parent;
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newBone);
		Main.instance.editorPanes.selector.updateBase();
		Main.instance.project.onAction(new HistoryAction(() -> {
			parent.removeEffect(newBone);
			main.project.updateSkeletonLocalAlt();
			if (Main.instance.getEditing() == newBone) Main.instance.setEditing(null);
			Main.instance.editorPanes.selector.updateBase();
		}, () -> {
			parent.addEffect(newBone);
			main.project.updateSkeletonLocalAlt();
			main.setEditing(newBone);
			Main.instance.editorPanes.selector.updateBase();
		}));
	}
}