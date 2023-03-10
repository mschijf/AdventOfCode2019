package adventofcode2019.december23

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

fun main() {
    PuzzleSolver(test=false).showResult()
}

//
// note this program prints the answer, but does not end.
//
class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): Any {
        val network= Network(inputLines.first())
        network.runNetwork()
        return super.resultPartOne()
    }
}

private const val DELAY_TIME = 1L

class Network(inputLine: String) {
    private val computerList = (0 ..49).associateWith { number -> Computer(inputLine, this, number) }
    private var lastPacket = Packet(0,0)

    fun runNetwork() = runBlocking {
        val launchList = mutableListOf<Job>()
        computerList.values.forEach {
            val job = launch {
                it.start()
            }
            launchList.add(job)
        }
        doNatControl()
        launchList.forEach { job -> job.cancel() }
    }

    private suspend fun doNatControl() {
        val idleYValueSet = mutableSetOf<Long>()
        while (true) {
            delay(DELAY_TIME)
            val allIdle = computerList.values.all { it.isIdle() }
            if (allIdle) {
                if (lastPacket.y in idleYValueSet) {
                    println("Part2: Sending packet to address 0 with y-value second time 0: $lastPacket")
                    break
                }
                computerList[0]!!.receivePackage(lastPacket)
                idleYValueSet.add(lastPacket.y)
            }
        }
    }

    suspend fun sendPackage(address: Int, packet: Packet) {
        if (address == 255) {
            if (lastPacket == Packet(0,0)) {
                println("Part1: First packet with address 255; y-value is: $packet")
            }
            lastPacket = packet
        } else {
            computerList[address]!!.receivePackage(packet)
        }
    }
}

class Computer(
    inputLine: String,
    private val network: Network,
    private val number: Int) {

    private val computer = IntCodeProgramCR(inputLine)
    private val queue = Channel<Packet>(UNLIMITED)

    fun start() = runBlocking {
        launch {
            computer.runProgram()
        }
        computer.input.send(number.toLong())
        computer.input.send(-1L)

        launch {
            while (true) {
                val address = computer.output.receive().toInt()
                val packet = Packet(computer.output.receive(), computer.output.receive())
                //println("Sending from $number to $address : $packet")
                network.sendPackage(address, packet)
            }
        }

        while (true) {
            val packet = queue.receive()
            computer.input.send(packet.x)
            computer.input.send(packet.y)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun isIdle(): Boolean {
        return queue.isEmpty && computer.output.isEmpty && computer.input.isEmpty
    }

    suspend fun receivePackage(packet: Packet) {
        queue.send(packet)
    }


}

data class Packet(val x: Long, val y: Long)


