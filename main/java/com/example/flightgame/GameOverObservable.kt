package com.example.flightgame

interface GameOverObservable: GameOverObserver {

        val observers: ArrayList<GameOverObserver>

        fun add(observer: GameOverObserver) {
            observers.add(observer)
        }

        fun remove(observer: GameOverObserver) {
            observers.remove(observer)
        }

        fun sendUpdateEvent() {
            observers.forEach { it.updateGame() }
        }

}