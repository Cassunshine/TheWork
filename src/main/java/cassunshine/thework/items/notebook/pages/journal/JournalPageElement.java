package cassunshine.thework.items.notebook.pages.journal;

import net.minecraft.util.Identifier;

/**
 * Contains something to be drawn by the journal. Mostly used by the rendering system.
 */
public record JournalPageElement(Identifier type, Identifier data) {
}
