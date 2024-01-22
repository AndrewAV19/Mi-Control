public class ColorChangeEvent {
    private String selectedColor;

    public ColorChangeEvent(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getSelectedColor() {
        return selectedColor;
    }
}
