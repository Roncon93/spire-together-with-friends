package stwf.screens.components;

public abstract class BaseComponent implements ComponentInterface
{
    protected float x;
    protected float y;

    public BaseComponent()
    {
        this(0.0f, 0.0f);
    }

    public BaseComponent(float x, float y)
    {
        move(x, y);
    }

    @Override
    public void move(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update()
    {
    }
}
