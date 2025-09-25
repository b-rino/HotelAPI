## Dolphin-project for relationer og OrderSystem-project for DAO-Integrationstest! 
\#ActivityLogger-project for concurrency og Entity/DTO opdeling
### Hver opmærksom på to relevante branches i Dolphin (ManyToMany på to forskellige måder) 

## Noter

Typiske Lombok annotationer for hver entitet:

````
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@ToString.Exclude
````

Typiske JPA annotationer:
````
@Entity
@Id
@GeneratedValue
@OneToOne(mappedBy="xxxxx", cascade = CascadeType.xxx)
    @OneToMany(mappedBy = "xxx", cascade = CascadeType.xxx, fetch = FetchType.EAGER)
@ManyToOne
@Builder.Default  // <---- This one is necessary with @Builder
private Set<Fee> fees = new HashSet<>()
````

One-To-Many: Husk at bruge HashSets for at undgå dubletter

Hjælpemetode til bi-directional relationer:
````java
    public void addFee(Fee fee)
    {
        this.fees.add(fee);
        if (fee != null)
        {
            fee.setPerson(this);
        }
    }
````

````
Husk at indsætte entiteter i `Hibernate.config` filen
Husk at oprette en database
Husk at oprette `config.properties` i "resources" mappen
Husk du kan bruge systemvaraible i stedet for `config.properties`
````

````bash
DB_NAME = NameOfDB
DB_USERNAME = USERNAME
DB_PASSWORD = PASSWORD
````