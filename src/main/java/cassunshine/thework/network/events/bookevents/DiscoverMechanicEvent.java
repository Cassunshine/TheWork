package cassunshine.thework.network.events.bookevents;

import cassunshine.thework.TheWorkMod;
import cassunshine.thework.items.notebook.NotebookData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Makes a player discover a mechanic in their Alchemist Notebook.
 */
public class DiscoverMechanicEvent extends BookLearnEvent {
    public static final Identifier IDENTIFIER = new Identifier(TheWorkMod.ModID, "discover_mechanic");

    public Identifier mechanicId;

    public DiscoverMechanicEvent() {
        super(IDENTIFIER);
    }

    public DiscoverMechanicEvent(Identifier mechanicId) {
        super(IDENTIFIER);
        this.mechanicId = mechanicId;
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        super.writePacket(buf);

        buf.writeIdentifier(mechanicId);
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        super.readPacket(buf);

        mechanicId = buf.readIdentifier();
    }

    @Override
    public boolean applyToNotebook(NotebookData data) {
        return data.mechanicsSection.discoverMechanic(mechanicId.withPrefixedPath("mechanic/"));
    }
}
