package pokecube.core.handlers;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pokecube.core.PokecubeItems;
import pokecube.core.database.Database;
import pokecube.core.database.Database.EnumDatabase;
import pokecube.core.events.handlers.EventsHandler;
import pokecube.core.events.handlers.SpawnHandler;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.utils.PokecubeSerializer;
import thut.core.common.config.ConfigBase;
import thut.core.common.config.Configure;

public class Config extends ConfigBase
{
    public static final String spawning               = "spawning";
    public static final String database               = "database";
    public static final String world                  = "generation";
    public static final String mobAI                  = "ai";
    public static final String misc                   = "misc";
    public static final String client                 = "client";
    public static final String advanced               = "advanced";

    public static int          GUICHOOSEFIRSTPOKEMOB_ID;
    public static int          GUIDISPLAYPOKECUBEINFO_ID;
    public static int          GUIDISPLAYTELEPORTINFO_ID;
    public static int          GUIPOKECENTER_ID;
    public static int          GUIPOKEDEX_ID;
    public static int          GUIPOKEMOBSPAWNER_ID;
    public static int          GUIPC_ID;
    public static int          GUIPOKEMOB_ID;
    public static int          GUITRADINGTABLE_ID;

    public static Config       instance;

    // Misc Settings
    @Configure(category = misc)
    public String[]            defaultStarts          = {};
    @Configure(category = misc)
    public boolean             contributorStarters    = true;
    @Configure(category = misc)
    public boolean             loginmessage           = true;
    @Configure(category = misc)
    /** is there a choose first gui on login */
    public boolean             guiOnLogin             = true;
    @Configure(category = misc)
    /** does defeating a tame pokemob give exp */
    public boolean             pvpExp                 = false;
    @Configure(category = misc)
    public boolean             mysterygift            = true;
    @Configure(category = misc)
    public String              defaultMobs            = "";
    @Configure(category = misc)
    protected boolean          tableRecipe            = true;
    @Configure(category = misc)
    public double              scalefactor            = 1;

    // AI Related settings
    @Configure(category = mobAI)
    public int                 mateMultiplier         = 1;
    @Configure(category = mobAI)
    public int                 breedingDelay          = 4000;
    @Configure(category = mobAI)
    public int                 eggHatchTime           = 10000;
    @Configure(category = mobAI)
    /** do wild pokemobs which leave despawnRadius despawn immediately */
    public boolean             cull                   = false;
    @Configure(category = mobAI)
    /** Will lithovores eat gravel */
    public boolean             pokemobsEatGravel      = false;
    @Configure(category = mobAI)
    /** Is there a warning before a wild pok�mob attacks the player. */
    public boolean             pokemobagresswarning   = true;
    @Configure(category = mobAI)
    /** Distance to player needed to agress the player */
    public int                 mobAggroRadius         = 3;
    @Configure(category = mobAI)
    /** Approximate number of ticks before pok�mob starts taking hunger
     * damage */
    public int                 pokemobLifeSpan        = 8000;
    @Configure(category = mobAI)
    /** Capped damage to players by pok�mobs */
    public int                 maxPlayerDamage        = 10;
    @Configure(category = mobAI)
    /** Warning time before a wild pok�mob attacks a player */
    public int                 pokemobagressticks     = 50;
    @Configure(category = mobAI)
    /** Number of threads allowed for AI. */
    public int                 maxAIThreads           = 1;
    @Configure(category = mobAI)
    public boolean             pokemobsDamageOwner    = false;
    @Configure(category = mobAI)
    public boolean             pokemobsDamagePlayers  = true;
    @Configure(category = mobAI)
    public boolean             pokemobsDamageBlocks   = false;
    @Configure(category = mobAI)
    /** Do explosions occur and cause damage */
    public boolean             explosions             = true;
    @Configure(category = mobAI)
    public int                 attackCooldown         = 20;

    // World Gen and World effect settings
    @Configure(category = world)
    /** do meteors fall. */
    public boolean             meteors                = true;
    @Configure(category = world)
    public int                 meteorDistance         = 3000;
    @Configure(category = world)
    public boolean             doSpawnBuilding        = true;
    @Configure(category = world)
    public boolean             pokemartMerchant       = true;
    @Configure(category = world)
    public String              cave                   = "";
    @Configure(category = world)
    public String              surface                = "";
    @Configure(category = world)
    public String              rock                   = "";
    @Configure(category = world)
    public String              trees                  = "";
    @Configure(category = world)
    public String              plants                 = "";
    @Configure(category = world)
    public String              terrains               = "";
    @Configure(category = world)
    public String              industrial             = "";

    // Mob Spawning settings
    @Configure(category = spawning)
    /** Do monsters not spawn. */
    public boolean             deactivateMonsters     = false;
    @Configure(category = spawning)
    /** do monster spawns get swapped with shadow pokemobs */
    public boolean             disableMonsters        = false;
    @Configure(category = spawning)
    /** do animals not spawn */
    public boolean             deactivateAnimals      = false;
    @Configure(category = spawning)
    /** do Pokemobs spawn */
    public boolean             pokemonSpawn           = true;
    @Configure(category = spawning)
    /** This is also the radius which mobs spawn in. Is only despawn radius if
     * cull is true */
    public int                 maxSpawnRadius         = 64;
    @Configure(category = spawning)
    /** closest distance to a player the pokemob can spawn. */
    public int                 minSpawnRadius         = 16;
    @Configure(category = spawning)
    /** Minimum level legendaries can spawn at. */
    public int                 minLegendLevel         = 1;
    @Configure(category = spawning)
    /** Will nests spawn */
    public boolean             nests                  = false;
    @Configure(category = spawning)
    /** number of nests per chunk */
    public int                 nestsPerChunk          = 1;
    @Configure(category = spawning)
    /** To be used for nest retrogen. */
    public boolean             refreshNests           = false;
    @Configure(category = spawning)
    public int                 mobSpawnNumber         = 10;
    @Configure(category = spawning)
    public double              mobDensityMultiplier   = 1;
    @Configure(category = spawning)
    public int                 levelCap               = 50;
    @Configure(category = spawning)
    public boolean             shouldCap              = true;
    @Configure(category = spawning)
    String[]                   spawnFunctions         = { "0:(10^6)*(sin(x*10^-3)^8 + sin(y*10^-3)^8)", "1:10+r/130;r",
            "2:(10^6)*(sin(x*0.5*10^-3)^8 + sin(y*0.5*10^-3)^8)" };

    // Gui/client settings
    @Configure(category = client)
    public int[]               guiOffset              = { 0, 0 };
    @Configure(category = client)
    public boolean             guiDown                = true;
    @Configure(category = client)
    public boolean             autoSelectMoves        = false;
    @Configure(category = client)
    public boolean             moveAnimationCallLists = true;

    @Configure(category = advanced)
    String[]                   mystLocs               = {};
    @Configure(category = advanced)
    boolean                    resetTags              = false;
    @Configure(category = advanced)
    String[]                   extraValues            = { "3", "4.5" };

    @Configure(category = database)
    boolean                    forceDatabase          = true;
    @Configure(category = database)
    String[]                   configDatabases        = { "pokemobs", "moves" };

    /** List of blocks to be considered for the floor of a cave. */
    private List<Block>        caveBlocks             = new ArrayList<Block>();

    /** List of blocks to be considered for the surface. */
    private List<Block>        surfaceBlocks          = new ArrayList<Block>();

    /** List of blocks to be considered to be rocks for the purpose of rocksmash
     * and lithovore eating */
    private List<Block>        rocks                  = new ArrayList<Block>();

    /** List of blocks to be considered to be generic terrain, for dig to reduce
     * drop rates for */
    private List<Block>        terrain                = new ArrayList<Block>();

    private Config()
    {
        super(null);
    }

    public Config(File path)
    {
        super(path, new Config());
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        populateSettings();
        applySettings();
        save();
    }

    @Override
    public void applySettings()
    {
        SpawnHandler.MAX_DENSITY = mobDensityMultiplier;
        SpawnHandler.MAXNUM = mobSpawnNumber;
        if (breedingDelay < 600) breedingDelay = 1000;

        SpawnHandler.doSpawns = pokemonSpawn;
        SpawnHandler.lvlCap = shouldCap;
        SpawnHandler.capLevel = levelCap;
        SpawnHandler.loadFunctionsFromStrings(spawnFunctions);

        PokecubeSerializer.MeteorDistance = meteorDistance * meteorDistance;

        for (String loc : mystLocs)
        {
            PokecubeMod.giftLocations.add(loc);
        }

        EventsHandler.juiceChance = Double.parseDouble(extraValues[0]);
        EventsHandler.candyChance = Double.parseDouble(extraValues[1]);

        PokecubeItems.resetTimeTags = resetTags;
        if (resetTags) get(advanced, "resetTags", false).set(false);

        PokecubeMod.pokemobsDamageBlocks = pokemobsDamageBlocks;
        PokecubeMod.pokemobsDamageOwner = pokemobsDamageOwner;
        PokecubeMod.pokemobsDamagePlayers = pokemobsDamagePlayers;

        Database.FORCECOPY = forceDatabase;

        if (configDatabases.length != 2)
        {
            configDatabases = new String[] { "pokemobs", "moves" };
        }

        for (int i = 0; i < EnumDatabase.values().length; i++)
        {
            String[] args = configDatabases[i].split(",");
            for (String s : args)
            {
                Database.addDatabase(s.trim(), EnumDatabase.values()[i]);
            }
        }
    }

    @Override
    public Property get(String category, String key, String defaultValue, String comment, Property.Type type)
    {
        Property prop = super.get(category, key, defaultValue, comment, type);
        requiresRestart(prop);
        return prop;
    }

    public List<Block> getCaveBlocks()
    {
        return caveBlocks;
    }

    public List<Block> getRocks()
    {
        return rocks;
    }

    public List<Block> getSurfaceBlocks()
    {
        return surfaceBlocks;
    }

    public List<Block> getTerrain()
    {
        return terrain;
    }

    public void initDefaultStarts()
    {
        FMLCommonHandler.callFuture(new FutureTask<Object>(new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                try
                {
                    JsonParser parser = new JsonParser();
                    URL url = new URL(PokecubeMod.CONTRIBURL);
                    URLConnection con = url.openConnection();
                    con.setConnectTimeout(1000);
                    con.setReadTimeout(1000);
                    InputStream in = con.getInputStream();
                    JsonElement element = parser.parse(new InputStreamReader(in));
                    JsonElement element1 = element.getAsJsonObject().get("contributors");
                    JsonArray contribArray = element1.getAsJsonArray();
                    List<String> defaults = Lists.newArrayList(defaultStarts);
                    for (int i = 0; i < contribArray.size(); i++)
                    {
                        element1 = contribArray.get(i);
                        JsonObject obj = element1.getAsJsonObject();
                        String name = obj.get("username").getAsString();
                        String info = obj.get("info").getAsString();
                        if (info != null && !info.isEmpty()) defaults.add(name + ":" + info);
                    }
                    defaultStarts = defaults.toArray(new String[0]);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }));
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
    {
        if (eventArgs.modID.equals("pokecube"))
        {
            populateSettings();
            applySettings();
            save();
        }
    }

    public void requiresRestart(Property property)
    {
        // TODO see which need which.
        // property.setRequiresMcRestart(false);
        // property.setRequiresWorldRestart(false);
    }

    @Override
    public void save()
    {
        if (hasChanged())
        {
            super.save();
        }
    }

    public void seenMessage()
    {
        load();
        get(misc, "loginmessage", false).set(false);
        get(misc, "version", PokecubeMod.VERSION).set(PokecubeMod.VERSION);
        save();
    }

    public void setSettings()
    {
        load();
        populateSettings(true);
        applySettings();
        save();
    }
}
