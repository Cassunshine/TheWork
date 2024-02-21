package cassunshine.thework.client.gui.ingame.notebook;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public abstract class AlchemistNotebookScreen extends Screen {
    private static final String DEFAULT_PAGE = "nodes";

    private static final ImmutableMap<String, Supplier<AlchemistNotebookScreen>> SCREEN_GENERATORS = new ImmutableMap.Builder<String, Supplier<AlchemistNotebookScreen>>().put("node", AlchemistNotebookNodeScreen::new).build();

    public NbtCompound itemNbt;
    public NbtCompound pageNbt;

    public AlchemistNotebookScreen(Text title, String pageName) {
        super(title);

        itemNbt = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND).getOrCreateNbt();
        this.client = MinecraftClient.getInstance();

        pageNbt = itemNbt.getCompound(pageName);
        itemNbt.put(pageName, pageNbt);
    }

    public void openPage(String pageName) {
        if (pageName.isEmpty())
            pageName = DEFAULT_PAGE;

        itemNbt.putString("opened_page", pageName);

        String finalPageName = pageName;

        this.client.execute(() -> {
            client.setScreen(SCREEN_GENERATORS.get(finalPageName).get());
        });
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
