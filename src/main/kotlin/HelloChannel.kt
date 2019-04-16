package dev.bananaumai.hellochannel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() = runBlocking {

    println("[source1] (produce)")
    val source1 = produce<Int> {
        println("[source1] Here is inside of producer scope")
        for (x in 1..10) {
            delay(200)
            send(x)
        }
    }
    println("[source1] Start consuming source2 without coroutines")
    source1.consumeEach {
        println("[source1] $it")
    }

    println("[source2] (produce)")
    val source2 = produce<Int> {
        println("[source2] Here is inside of producer scope.")
        for (x in 1..10) {
            delay(200)
            send(x)
        }
    }
    println("[source2] Start consuming source2 in the coroutines")
    launch {
        source2.consumeEach {
            println("[source2] $it (First launched context)")
        }
    }
    launch {
        source2.consumeEach {
            println("[source2] $it (Second launched context)")
        }
    }

    println("[source3] (Channel)")
    val source3 = Channel<Int>()
    launch {
        println("[source3] Here is inside of producer scope")
        for (x in 1..10) {
            delay(200)
            source3.send(x)
        }
    }
    println("[source3] Start consuming source3 in the coroutines")
    launch {
        source3.consumeEach {
            println("[source3] $it (First launched context)")
        }
    }
    launch {
        source3.consumeEach {
            println("[source3] $it (Second launched context)")
        }
    }

    println("[source4] (produce - infinite)")
    val source4 = produce<Int> {
        var cnt = 0
        while (true) {
            println("[source4] Here is inside of producer scope $cnt")
            send(cnt)
            cnt++
        }
    }
    launch {
        repeat(10) {
            println("[source4] ${source4.receive()} (First launched context)")
        }
    }
    launch {
        repeat(10) {
            println("[source4] ${source4.receive()} (Second launched context)")
        }
    }

    println("Here is the end of runBlocking scope")
}
