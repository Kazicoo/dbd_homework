

# Moving

## C2S

`KeyDown;<Key: String>` - Key down event.
`KeyUp;<Key: String>` - Key up event.

## S2C

`Move;<Id: int>;<X: int>;<Y: int>` - Move entity with id to x, y.


# Attack

## C2S

`Attack` - Attack event.

## S2C

`Attack;<Facing: int>` - Attack event.
`Hit;<Id: int>` - Human with id was hit.
`Fall;<Id: int>` - Human with id fell.
`Dead;<Id: int>` - Human with id died.


# Healing

## C2S

`Heal;<PlayerIdToHeal; int>` - Heal player with id.
`StopHeal` - Stop healing.

## S2C

`Healing;<HealerId: int>;<HealeeId: int>` - Healer healed healee.
`Healed;<HealeeId: int>` - Healee was healed.
