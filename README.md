# Highlight Closed Doors

Highlights the clickbox of visible closed doors and gates with a cyan outline. An object is considered closed when its active object definition is named like a door or gate and offers an `Open` action. Open objects, chests, cupboards, and other unrelated scenery are ignored.

The plugin checks live object actions every game tick, including operation overrides and quest objects that transform in place. Wall, game, decorative, and ground door objects are supported. If an object has no clickbox shape, its model hull is used as a visual fallback.

```text
./gradlew -p highlight-closed-doors clean test
```
