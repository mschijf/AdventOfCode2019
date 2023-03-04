package adventofcode2019.december23

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): Any {
        val network= Network(inputLines.first())
        network.runNetwork()
        return super.resultPartOne()
    }
}

class Network(inputLine: String) {
    private val computerList = (0 ..49).associateWith { number -> Computer(this, inputLine, number) }
    private var lastPacket = Packet(0,0)

    fun runNetwork() = runBlocking {
        computerList.values.forEach {
            launch {
                it.start()
            }
        }
        launch {
            doNatControl()
        }
    }

    val ySet = mutableSetOf<Long>()
    private suspend fun doNatControl() {
        while (true) {
            delay(10)
            val allIdle = computerList.values.all { it.isIdle() }
            if (allIdle) {
                if (lastPacket.y in ySet) {
                    println("===================================== Sending seond time idle packet to 0: $lastPacket")
                }
                computerList[0]!!.receivePackage(lastPacket)
                ySet.add(lastPacket.y)
            }
        }
    }

    fun sendPackage(address: Int, packet: Packet) {
//        println("sending to address --> $address: $packet")
        if (address == 255) {
//            println("===================================== message to NAT: $packet")
            lastPacket = packet
        } else {
            computerList[address]!!.receivePackage(packet)
        }
    }

}

class Computer(private val network: Network, inputLine: String, val number: Int) {

    private val computer = IntCodeProgramCR(inputLine)

    private val queue = ArrayDeque<Packet>()

    fun start() = runBlocking {
        launch {
            computer.runProgram()
            println("$number is done")
        }
        computer.input.send(number.toLong())

        launch {
            while (!computer.output.isClosedForReceive) {
                runQueue()
                while (queue.isEmpty()) {
                    delay(10)
                }
            }
        }

        launch {
            while (!computer.output.isClosedForReceive) {
                sendPackage()
            }
        }
    }

    fun isIdle(): Boolean {
//        println("$number is asked for being idle")
        return queue.isEmpty() && computer.output.isEmpty && computer.input.isEmpty
    }

    private suspend fun runQueue() {
//        println("$number runs runQueue (size: ${queue.size})")
        while (queue.isNotEmpty()) {
//            println("$number has items on queue (size: ${queue.size})")
            val packet = queue.removeFirst()
            computer.input.send(packet.x)
            computer.input.send(packet.y)
        }
        computer.input.send(-1L)
    }

    fun receivePackage(packet: Packet) {
//        println("$number gets incoming message: $packet")
        queue.add(packet)
    }

    private suspend fun sendPackage() {
        val address = computer.output.receive().toInt()
        val packet = Packet(computer.output.receive(), computer.output.receive())
//        println("from $number to $address, $packet")
        network.sendPackage(address, packet)
    }

}

data class Packet(val x: Long, val y: Long)


