import React, {Component} from "react";
import {Scale, Scales} from "./Scale";

export class Home extends Component<{}, { scales: Scales }> {
    componentDidMount() {
        fetch(`http://localhost:9000/scales`)
            .then(result => result.json())
            .then(s => this.setState({scales: s}))
    }

    render() {
        return <span>
            <h1>Borgy</h1>
            <span id={"newTrial"}>
                <fieldset>
                    <legend>Start a new trial</legend>
                    <label htmlFor="trial-name">Name</label><input id="trial-name" type="text"/><br/>
                    <label>Scale</label><br/>
                    {this.state ? this.state.scales.map(Home.renderScaleRadio) : <span>Loading scales</span>}
                    <button>Begin</button>
                </fieldset>
            </span>
        </span>
    }

    private static renderScaleRadio(scale: Scale) {
        let radioName = `scale-${scale.name}`;
        let scaleName = `${scale.name}`;
        return <span>
            <input type="radio" name={radioName} value={scaleName}/>
            <label htmlFor={radioName}>{scaleName}</label><br/>
        </span>;
    }
}