package com.firemerald.mcms.mc4;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.RenderBone;
import firemerald.mcms.api.model.effects.EffectRenderStage;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.decoration.DecoPane;
import firemerald.mcms.util.history.HistoryAction;

public class GuiPopupAssemblyTerminalEffect<T extends RenderBone<T>> extends GuiPopup
{
	public final T parent;
	public final DecoPane pane;
	private ComponentText name;
	public final StandardButton ok, cancel;
	
	public GuiPopupAssemblyTerminalEffect(T parent)
	{
		this.parent = parent;
		final int cw = 240;
		final int ch = 60;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		this.addElement(name = new ComponentText(cx, y, cx + cw, y + 20, Main.instance.fontMsg, "assembly_input", name -> {}) {
			@Override
			public boolean shouldUndo()
			{
				return false;
			}
		});
		y += 20;
		
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 240;
		final int ch = 60;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, y, cx + cw, y + 20);
		y += 20;
		
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
		final AssemblyTerminalEffect newBone = new AssemblyTerminalEffect(name.getText(), parent, EffectRenderStage.POST_CHILDREN);
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