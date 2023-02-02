# Wave Secret Decoder
”Logic will get you from A to B. Imagination will take you everywhere.” - Albert Einstein

It is a library for getting a human-readable string from a Wav file.
 
 
## Technologies
 
The library is built in a multi-module structure and attempts to use the latest tools and libraries.
 
* 100% uses Kotlin.
* Uses Kotlin Coroutines throughout threading.
* Uses Flow for sequential programming.
* Write 39 unit tests to ensure everything works fine.
 
## Modules
 
This project has 3 different modules with names:
 
* **flowutils** : Is used to place custom flow utils. 
* **wavedecoder**: Is used to define wave decoder helper methods for decoding Wav file.
* **WaveSecretDecoder**: Is used to handle the challenge logics.

## Solution
  
I used these tools to figure out the final solution:
 
* **Audacity** software to visualize the Wav file frequency and wavelength.
* **HxD** software to observe the hex values of that Wav file
* Decoding Audio Modems with Audacity using [this article](https://medium.com/poka-techblog/back-to-basics-decoding-audio-modems-with-audacity-c94faa8362a0)
 
 
## Implementation

As mentioned in the description it is a challenging task to finish but I tried to do my best.

The code procedures are developed at a high level as you can see the eager methods called step by step after each other:
 
```kotlin
fun File.findSecretMessage(): Flow<String> {
    return readWaveFileBytes()
        .detectSoundHeight()
        .detectFrequencies()
        .demodulate()
        .convertToOrderedBytes()
        .convertToInt()
        .retrieveData()
        .removeCheckSum()
        .convertToString()
}
```
 
In the `retrieveData` method you can apply the challenge logic like this:
 
```kotlin
fun Flow<Int>.retrieveData(): Flow<Int> {
    var buffer = listOf<Int>()
    return dropWhile {
        it != LEADER_VALUE
    }
        .skipOccurrenceSequence(LEADER_VALUE, LEADER_BYTE_SIZE)
        .skipOccurrenceSequence(ID_FIRST_VALUE, ID_BYTE_SIZE)
        .skipOccurrenceSequence(ID_SECOND_VALUE, ID_BYTE_SIZE)
        .skipOccurrenceSequenceWithData(MESSAGE_BYTE_SIZE) {
            buffer = it
        }
        .skipOccurrenceSequence(DIVIDER_VALUE, DIVIDER_SIZE)
        .skipOccurrenceSequence(LEADER_VALUE, END_BLOCK_BYTE_SIZE, stopOnFind = true)
        .onCompletion {
            emitAll(buffer.asFlow())
        }
}
 
```
 
## Result
 
```
It was the White Rabbit, trotting slowly back again, and looking anxiously about as it went, as if it had lost something; and she heard it muttering to itself `The Duchess! The Duchess! Oh my dear paws! Oh my fur and whiskers! She'll get me executed, as sure as ferrets are ferrets! Where can I have dropped them, I wonder?' Alice guessed in a moment that it was looking for the fan and the pair of white kid gloves, and she very good-naturedly began hunting about for them, but they were nowhere to be seen--everything seemed to have changed since her swim in the pool, and the great hall, with the glass table and the little door, had vanished completely.
Very soon the Rabbit noticed Alice, as she went hunting about, and called out to her in an angry tone, `Why, Mary Ann, what are you doing out here? Run home this moment, and fetch me a pair of gloves and a fan! Quick, now!' And Alice was so much frightened that she ran off at once in the direction it pointed to, without trying to explain the mistake it had made.
`He took me for his housemaid,' she said to herself as she ran. `How surprised he'll be when he finds out who I am! But I'd better take him his fan and gloves--that is, if I can find them.' As she said this, she came upon a neat little house, on the door of which was a bright brass plate with the name `W. RABBIT' engraved upon it. She went in without knocking, and hurried upstairs, in great fear lest she should meet the real Mary Ann, and be turned out of the house before she had found the fan and gloves.
`How queer it seems,' Alice said to herself, `to be going messages for a rabbit! I suppose Dinah'll be sending me on messages next!' And she began fancying the sort of thing that would happen: `"Miss Alice! Come here directly, and get ready for your walk!" "Coming in a minute, nurse! But I've got to see that the mouse doesn't get out." Only I don't think,' Alice went on, `that they
```
 
## Issues
 
There are two issues with this code challenge:
 
* The end block length was 129 BYTES instead of 130 that mentioned in the documentation
* Abraham Lincoln died many years ago in 1865. How could he say something about the Internet before creating that? 
