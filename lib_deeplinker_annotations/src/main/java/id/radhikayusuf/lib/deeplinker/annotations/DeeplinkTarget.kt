package id.radhikayusuf.lib.deeplinker.annotations

interface DeeplinkTarget<A, B, C> {
    val deeplinks: Map<String, DeeplinkMap<A, B, C>>
}
