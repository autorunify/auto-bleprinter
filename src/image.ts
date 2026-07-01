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
    this.colors = encode(values as ArrayBuffer)
  }
}
