package jeiwt.capability.WorldTooltipOverride;

public class WorldTooltipOverride implements IWorldTooltipOverride {

    private String displayName = "";
    private String description = "";

    public WorldTooltipOverride() {

    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
