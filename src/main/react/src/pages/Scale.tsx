export type Intensity = {
    number: Number,
    label: String
};

export type Scale = {
    name: String,
    description: String,
    intensities: Array<Intensity>
}

export type Scales = Array<Scale>
export type Trials = Array<String>
