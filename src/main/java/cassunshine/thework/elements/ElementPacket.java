package cassunshine.thework.elements;

public record ElementPacket(Element element, int amount) {


    @Override
    public String toString() {
        return String.format("Element Packet %s %d", element, amount);
    }
}
