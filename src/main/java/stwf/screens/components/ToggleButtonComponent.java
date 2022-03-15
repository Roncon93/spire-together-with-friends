package stwf.screens.components;

public abstract class ToggleButtonComponent extends BaseButtonComponent
{
    boolean isToggled;
    boolean canBeDeselected;

    public ToggleButtonComponent()
    {
        super();

        isToggled = false;
        canBeDeselected = true;
    }

    @Override
    protected void onClick()
    {
        super.onClick();

        if (canBeDeselected)
        {
            isToggled = !isToggled;
        }
        else if (!isToggled)
        {
            isToggled = true;
        }
    }
}
