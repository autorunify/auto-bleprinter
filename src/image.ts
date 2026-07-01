import { encode } from "base64-arraybuffer"

export class ImageCompressor {
  width: number
  height: number
  colors: string

  constructor(imageData: ImageData) {
    const data = imageData.data

    this.width = imageData.width
    this.height = imageData.height
    this.colors = ""

    const pixels = this.width * this.height
    const colorsType = imageData.data.byteLength / (pixels)
    if (colorsType != 4) throw Error("imageData type not Uint8ClampedArray")

    const values = data.values() as any
    this.colors = this.RLE(encode(values as ArrayBuffer))
  }

  private RLE(value: string): string {
    if (value.length == 0) return ""

    var rle = ""
    var count = 1

    for (var idx = 0; idx < value.length; idx++) {
      if (value[idx] == value[idx - 1]) {
        count++
      } else {
        rle += value.charAt(idx - 1)
        if (count > 1) {
          rle += '!'
          rle += count.toString(16)
          rle += '!'
        }
        count = 1
      }
    }

    rle += value.charAt(idx - 1)
    if (count > 1) {
      rle += '!'
      rle += count.toString(16)
      rle += '!'
    }

    return rle
  }
}
