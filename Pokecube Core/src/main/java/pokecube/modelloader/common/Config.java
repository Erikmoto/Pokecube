package pokecube.modelloader.common;

import java.io.File;

import thut.core.common.config.ConfigBase;

public class Config extends ConfigBase
{
    public Config()
    {
        super(null);
    }
    
    public Config(File path)
    {
        super(path, new Config());
    }

    @Override
    protected void applySettings()
    {
        // TODO Auto-generated method stub

    }

}
