namespace Model.Domain;

public class Entity<ID>
{
    public ID id{get;set;}

    public Entity(ID id)
    {
        this.id = id;
    }
}