import pydicom
import numpy

ds = pydicom.dcmread(filePath)
data_element = ds[0x3004,0x000E]
doseData = numpy.zeros(ds.pixel_array.size, dtype=float)
# Multiply pixel array with dose correction factor
doseData = numpy.multiply(ds.pixel_array.flat, data_element.value)