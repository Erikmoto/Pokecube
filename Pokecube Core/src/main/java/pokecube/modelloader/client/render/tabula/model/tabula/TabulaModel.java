package pokecube.modelloader.client.render.tabula.model.tabula;

import pokecube.modelloader.client.render.tabula.json.JsonTabulaModel;
import pokecube.modelloader.client.render.tabula.model.IModel;

public class TabulaModel extends JsonTabulaModel implements IModel {
    
    private String modelName;
    private String authorName;

    @Override
    public String getAuthor() {
        return authorName;
    }

    @Override
    public String getName() {
        return modelName;
    }
}
