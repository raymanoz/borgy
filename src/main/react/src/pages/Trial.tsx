import React, {Component} from "react";
import Button from "../components/button";

type Intensity = {
    number: Number,
    label: String
};

type Scale = {
    name: String,
    description: String,
    intensities: Array<Intensity>
}

type Scales = Array<Scale>

export class Trial extends Component<{}, {items: Scales}> {
    componentDidMount() {
        fetch(`http://localhost:9000/scales`)
            .then(result => result.json())
            .then(scales => {
                    return this.setState({items: scales})
                }
            )
    }

    render() {
        return (this.state ? this.buttons(this.state.items[0]) : <span/>)
    }

    private buttons(scale: Scale) {
        return <div>{scale.description}<br/>
                <div>{scale.intensities.map((intensity, index) => <Button key={index} intensity={intensity.number} label={intensity.label}/>)}</div>
        </div>;
    }
}