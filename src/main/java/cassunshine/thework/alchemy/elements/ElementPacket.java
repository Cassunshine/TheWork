package cassunshine.thework.alchemy.elements;

public record ElementPacket(Element element, float amount) {
    @Override
    public String toString() {
        return String.format("Element Packet %s %f", element, amount);
    }
}
