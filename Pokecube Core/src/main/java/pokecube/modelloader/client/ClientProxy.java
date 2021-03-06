package pokecube.modelloader.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import pokecube.core.client.render.entity.RenderAdvancedPokemobModel;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.modelloader.CommonProxy;
import pokecube.modelloader.ModPokecubeML;
import pokecube.modelloader.client.gui.GuiAnimate;
import pokecube.modelloader.client.render.AnimationLoader;
import pokecube.modelloader.client.render.TabulaPackLoader;
import pokecube.modelloader.items.ItemModelReloader;

public class ClientProxy extends CommonProxy
{

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return new GuiAnimate();
    }

    @Override
    public void init()
    {
        super.init();
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemModelReloader.instance, 0,
                new ModelResourceLocation("pokecube_ml:modelreloader", "inventory"));
    }

    @Override
    public void populateModels()
    {
        TabulaPackLoader.clear();

        List<String> modList = Lists.newArrayList(modelProviders.keySet());
        // Sort to prioritise default mod
        Collections.sort(modList, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                if (o1.equals(PokecubeMod.defaultMod)) return Integer.MAX_VALUE;
                else if (o2.equals(PokecubeMod.defaultMod)) return Integer.MIN_VALUE;
                return o1.compareTo(o2);
            }
        });

        ProgressBar bar = ProgressManager.push("Model Locations", modList.size());
        for (String mod : modList)
        {
            bar.step(mod);
            ProgressBar bar2 = ProgressManager.push("Pokemob", Database.allFormes.size());
            for (PokedexEntry p : Database.allFormes)
            {
                bar2.step(p.getName());
                try
                {
                    ResourceLocation tex = new ResourceLocation(mod, AnimationLoader.MODELPATH + p.getName() + ".xml");
                    IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                    res.getInputStream().close();
                    ArrayList<String> models = modModels.get(mod);
                    if (models == null)
                    {
                        modModels.put(mod, models = new ArrayList<String>());
                    }
                    if (!models.contains(p.getName())) models.add(p.getName());
                }
                catch (Exception e)
                {
                    try
                    {
                        ResourceLocation tex = new ResourceLocation(mod,
                                AnimationLoader.MODELPATH + p.getName() + ".tbl");
                        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                        res.getInputStream().close();
                        ArrayList<String> models = modModels.get(mod);
                        if (models == null)
                        {
                            modModels.put(mod, models = new ArrayList<String>());
                        }
                        if (!models.contains(p.getName())) models.add(p.getName());
                    }
                    catch (Exception e1)
                    {
                        try
                        {
                            ResourceLocation tex = new ResourceLocation(mod,
                                    AnimationLoader.MODELPATH + p.getName() + ".x3d");
                            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(tex);
                            res.getInputStream().close();
                            ArrayList<String> models = modModels.get(mod);
                            if (models == null)
                            {
                                modModels.put(mod, models = new ArrayList<String>());
                            }
                            if (!models.contains(p.getName())) models.add(p.getName());
                        }
                        catch (Exception e2)
                        {

                        }
                    }
                }
            }
            ProgressManager.pop(bar2);
            if (modModels.containsKey(mod))
            {
                HashSet<String> alternateFormes = Sets.newHashSet();
                bar2 = ProgressManager.push("Pokemob", modModels.get(mod).size());
                for (String s : modModels.get(mod))
                {
                    bar2.step(s);
                    if (!AnimationLoader.initModel(mod + ":" + AnimationLoader.MODELPATH + s, alternateFormes))
                    {
                        TabulaPackLoader.loadModel(mod + ":" + AnimationLoader.MODELPATH + s, alternateFormes);
                    }
                }
                ProgressManager.pop(bar2);
                for (String s : alternateFormes)
                {
                    if (!AnimationLoader.initModel(s, alternateFormes))
                    {
                        TabulaPackLoader.loadModel(s, alternateFormes);
                    }
                }
            }
        }
        ProgressManager.pop(bar);
        TabulaPackLoader.postProcess();
        registerRenderInformation();
    }

    @Override
    public void postInit()
    {
        super.postInit();
        AnimationLoader.loaded = true;
    }

    @Override
    public void preInit()
    {
        super.preInit();
        OBJLoader.instance.addDomain(ModPokecubeML.ID);
        B3DLoader.instance.addDomain(ModPokecubeML.ID);
    }

    @Override
    public void registerModelProvider(String modid, Object mod)
    {
        super.registerModelProvider(modid, mod);
        if (!modelProviders.containsKey(modid)) modelProviders.put(modid, mod);
    }

    @Override
    public void registerRenderInformation()
    {
        for (String modid : modelProviders.keySet())
        {
            Object mod = modelProviders.get(modid);
            if (modModels.containsKey(modid))
            {
                for (String s : modModels.get(modid))
                {
                    if (AnimationLoader.models.containsKey(s))
                    {
                        PokecubeMod.getProxy().registerPokemobRenderer(s, new RenderAdvancedPokemobModel<>(s, 1), mod);
                    }
                }
            }
        }
        for (PokedexEntry entry : TabulaPackLoader.modelMap.keySet())
        {
            if (entry == null) continue;

            Object mod = null;
            for (String modid : modelProviders.keySet())
            {
                if (modid.equalsIgnoreCase(entry.getModId()))
                {
                    mod = modelProviders.get(modid);
                    break;
                }
            }
            if (mod != null) PokecubeMod.getProxy().registerPokemobRenderer(entry.getName(),
                    new RenderAdvancedPokemobModel<>(entry.getName(), 1), mod);
        }
    }
}
