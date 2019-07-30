export type Intensity = {
    number: Number,
    label: string
};

export type Scale = {
    name: string,
    description: string,
    intensities: Array<Intensity>
}

export type Scales = Array<Scale>
export type Trials = Array<string>
