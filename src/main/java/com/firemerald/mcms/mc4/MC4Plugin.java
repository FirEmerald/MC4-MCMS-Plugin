package com.firemerald.mcms.mc4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import firemerald.mcms.api.model.effects.BoneEffect;
import firemerald.mcms.api.model.effects.EffectRenderStage;
import firemerald.mcms.events.ApplicationEvent;
import firemerald.mcms.events.EventHandler;
import firemerald.mcms.plugin.Plugin;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.font.Formatting;

@Plugin(author = Formatting.FIREMERALD, id = MC4Plugin.ID, name = MC4Plugin.NAME, version = "0.0.1", icon = MC4Plugin.ICON, description = MC4Plugin.DESCRIPTION)
public class MC4Plugin
{
	public static final Logger LOGGER = LogManager.getLogger("MC4 Plugin");
	public static final String ID = "mc4";
	public static final String NAME = "MC4 Plugin";
	public static final String ICON = ID + ":icon.png";
	public static final String DESCRIPTION = "Plugin for MC4 features";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@EventHandler
	public void onEvent(ApplicationEvent.Initialization event)
	{
		//register bone effects
		BoneEffect.registerBoneEffect(new ResourceLocation(ID, "display"), "gunship display", GunshipDisplayEffect.DISPLAY_TEX, bone -> new GuiPopupGunshipDisplayEffect(bone).activate(), (bone, el, scale) -> {
			GunshipDisplayEffect effect = new GunshipDisplayEffect(el.getString("name", "unnamed item"), bone);
			effect.loadFromXML(el, scale);
			return effect;
		});
		BoneEffect.registerBoneEffect(new ResourceLocation(ID, "assembly_terminal"), "assembly input", AssemblyTerminalEffect.DISPLAY_TEX, bone -> new GuiPopupAssemblyTerminalEffect(bone).activate(), (bone, el, scale) -> {
			AssemblyTerminalEffect effect = new AssemblyTerminalEffect(el.getString("name", "unnamed item"), bone, EffectRenderStage.POST_CHILDREN);
			effect.loadFromXML(el, scale);
			return effect;
		});
	}
}