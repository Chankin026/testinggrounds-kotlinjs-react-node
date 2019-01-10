object Main {

    val name get() = "be-node"
}

fun main(args: Array<String>) {
    println("Im running 'main'")
    if(System.getenv("DO_START") == "true"){
        println("Start!")
    }else{
        println("No start. Add arg '--start' to start application")
        println("${args.toList()}")
    }
}
