package pokecube.core.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.AbilityManager;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.utils.Tools;
import thut.api.entity.IMobColourable;
import thut.api.maths.Vector3;

public class MakeCommand extends CommandBase
{
    private List<String> aliases;

    public MakeCommand()
    {
        this.aliases = new ArrayList<String>();
        this.aliases.add("pokemake");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        List<String> ret = new ArrayList<String>();
        if (args.length == 1)
        {
            String text = args[0];
            for (PokedexEntry entry : Database.allFormes)
            {
                String check = entry.getName().toLowerCase();
                if (check.startsWith(text.toLowerCase()))
                {
                    String name = entry.getName();
                    if (name.contains(" "))
                    {
                        name = "\'" + name + "\'";
                    }

                    ret.add(name);
                }
            }
            Collections.sort(ret, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    if (o1.contains("'") && !o2.contains("'")) return 1;
                    else if (o2.contains("'") && !o1.contains("'")) return -1;
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }
        return ret;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<String> getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public String getCommandName()
    {
        return aliases.get(0);
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + aliases.get(0) + "<pokemob name> <arguments>";
    }

    @Override
    /** Return the required permission level for this command. */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        String text = "";
        IChatComponent message;
        EntityPlayerMP[] targets = null;
        for (int i = 1; i < args.length; i++)
        {
            String s = args[i];
            if (s.contains("@"))
            {
                ArrayList<EntityPlayer> targs = new ArrayList<EntityPlayer>(
                        PlayerSelector.matchEntities(sender, s, EntityPlayer.class));
                targets = targs.toArray(new EntityPlayerMP[0]);
            }
        }
        boolean isOp = CommandTools.isOp(sender);

        boolean deobfuscated = PokecubeMod.isDeobfuscated();
        boolean commandBlock = !(sender instanceof EntityPlayer);

        if (deobfuscated || commandBlock)
        {
            String name;
            IPokemob mob = null;
            if (args.length > 0)
            {
                int num = 1;
                int index = 1;
                EntityPlayer player = null;

                if (targets != null)
                {
                    num = targets.length;
                }
                for (int i = 0; i < num; i++)
                {
                    if (isOp || !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
                    {
                        PokedexEntry entry = null;
                        try
                        {
                            int id = Integer.parseInt(args[0]);
                            entry = Database.getEntry(id);
                            name = entry.getName();
                        }
                        catch (NumberFormatException e)
                        {
                            name = args[0];
                            if (name.startsWith("\'"))
                            {

                                for (int j = 1; j < args.length; j++)
                                {
                                    name += " " + args[j];
                                    if (args[j].contains("\'"))
                                    {
                                        index = j + 1;
                                        break;
                                    }
                                }
                            }
                            entry = Database.getEntry(name);
                        }
                        if (entry == null)
                        {

                        }

                        mob = (IPokemob) PokecubeMod.core.createEntityByPokedexNb(entry.getPokedexNb(),
                                sender.getEntityWorld());

                        if (mob == null)
                        {
                            CommandTools.sendError(sender, "pokecube.command.makeinvalid");
                            return;
                        }
                        mob.changeForme(name);
                        Vector3 offset = Vector3.getNewVector().set(0, 1, 0);

                        String owner = "";
                        boolean shiny = false;
                        boolean shadow = false;
                        int red, green, blue;
                        byte gender = -3;
                        red = green = blue = 255;
                        boolean ancient = false;
                        String ability = null;

                        int exp = 10;
                        int level = -1;
                        String[] moves = new String[4];
                        int mindex = 0;

                        if (index < args.length)
                        {
                            for (int j = index; j < args.length; j++)
                            {
                                String[] vals = args[j].split(":");
                                String arg = vals[0];
                                String val = "";
                                if (vals.length > 1) val = vals[1];
                                if (arg.equalsIgnoreCase("s"))
                                {
                                    shiny = true;
                                }
                                if (arg.equalsIgnoreCase("sh"))
                                {
                                    shadow = true;
                                }
                                else if (arg.equalsIgnoreCase("l"))
                                {
                                    level = Integer.parseInt(val);
                                    exp = Tools.levelToXp(mob.getExperienceMode(), level);
                                }
                                else if (arg.equalsIgnoreCase("x"))
                                {
                                    if (val.equalsIgnoreCase("f")) gender = IPokemob.FEMALE;
                                    if (val.equalsIgnoreCase("m")) gender = IPokemob.MALE;
                                }
                                else if (arg.equalsIgnoreCase("r"))
                                {
                                    red = Integer.parseInt(val);
                                }
                                else if (arg.equalsIgnoreCase("g"))
                                {
                                    green = Integer.parseInt(val);
                                }
                                else if (arg.equalsIgnoreCase("b"))
                                {
                                    blue = Integer.parseInt(val);
                                }
                                else if (arg.equalsIgnoreCase("o"))
                                {
                                    owner = val;
                                }
                                else if (arg.equalsIgnoreCase("a"))
                                {
                                    ability = val;
                                }
                                else if (arg.equalsIgnoreCase("m") && mindex < 4)
                                {
                                    moves[mindex] = val;
                                    mindex++;
                                }
                                else if (arg.equalsIgnoreCase("v"))
                                {
                                    String[] vec = val.split(",");
                                    offset.x = Double.parseDouble(vec[0].trim());
                                    offset.y = Double.parseDouble(vec[1].trim());
                                    offset.z = Double.parseDouble(vec[2].trim());
                                }
                            }
                        }

                        Vector3 temp = Vector3.getNewVector();
                        if (player != null)
                        {
                            offset = offset.add(temp.set(player.getLookVec()));
                        }
                        temp.set(sender.getPosition()).addTo(offset);
                        temp.moveEntity((Entity) mob);

                        if (targets != null)
                        {
                            player = targets[i];
                            if (player != null)
                            {
                                owner = targets[i].getUniqueID().toString();
                            }
                            else
                            {
                                owner = "";
                            }
                        }
                        else
                        {
                            EntityPlayer p = sender.getEntityWorld().getPlayerEntityByName(owner);
                            if (p != null) owner = p.getUniqueID().toString();
                        }

                        mob.setHp(((EntityLiving) mob).getMaxHealth());
                        if (!owner.equals(""))
                        {
                            mob.setPokemonOwnerByName(owner);
                            mob.setPokemonAIState(IMoveConstants.TAMED, true);
                        }
                        mob.setShiny(shiny);
                        if (gender != -3) mob.setSexe(gender);
                        if (mob instanceof IMobColourable) ((IMobColourable) mob).setRGBA(red, green, blue, 255);
                        if (shadow) mob.setShadow(shadow);
                        if (ancient) mob.setAncient(ancient);
                        mob.setExp(exp, true, true);
                        if (AbilityManager.abilityExists(ability)) mob.setAbility(AbilityManager.getAbility(ability));

                        for (int i1 = 0; i1 < 4; i1++)
                        {
                            if (moves[i1] != null)
                            {
                                String arg = moves[i1];
                                if (!arg.isEmpty())
                                {
                                    if (arg.equalsIgnoreCase("none"))
                                    {
                                        mob.setMove(i1, null);
                                    }
                                    else
                                    {
                                        mob.setMove(i1, arg);
                                    }
                                }
                            }
                        }
                        mob.specificSpawnInit();
                        if (mob != null)
                        {
                            ((Entity) mob).worldObj.spawnEntityInWorld((Entity) mob);
                        }
                        text = EnumChatFormatting.GREEN + "Spawned " + mob;
                        message = IChatComponent.Serializer.jsonToComponent("[\"" + text + "\"]");
                        sender.addChatMessage(message);
                        return;
                    }
                }
            }
            CommandTools.sendError(sender, "pokecube.command.makeneedname");
            return;
        }
        CommandTools.sendError(sender, "pokecube.command.makedeny");
        return;
    }

}
